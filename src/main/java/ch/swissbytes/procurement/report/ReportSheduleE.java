package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.processor.Processor;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ClassEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.report.dtos.ClausesReportDto;
import ch.swissbytes.procurement.report.dtos.PurchaseOrderReportDto;
import ch.swissbytes.procurement.report.dtos.PurchaseOrderSummaryDto;
import ch.swissbytes.procurement.util.ImageUtil;
import ch.swissbytes.procurement.util.ResourceUtils;
import ch.swissbytes.procurement.util.XmlWorker;
import com.itextpdf.text.DocumentException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportSheduleE extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportSheduleE.class.getName());
    private Configuration configuration;
    private ResourceUtils resourceUtils;
    private PurchaseOrderEntity po;
    private List<ItemEntity> itemEntityList;
    private String preamble;
    private List<ClausesEntity> clausesList;
    private CashflowEntity cashflowEntity;
    private EntityManager entityManager;
    private int nivel = 1;
    private boolean draft;
    private BigInteger[] currenciesIdsPo = new BigInteger[3];
    private List<PODocumentEntity> poDocumentList;
    private List<ByteArrayOutputStream> otherDocumentList = new ArrayList<>();
    //private


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportSheduleE(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                          Configuration configuration, PurchaseOrderEntity po, List<ItemEntity> itemEntityList,
                          String preamble, List<ClausesEntity> clausesList, CashflowEntity cashflowEntity, EntityManager entityManager, boolean draft, List<PODocumentEntity> poDocumentList,
                          DataSource ds) throws IOException, DocumentException {
        super(filenameJasper, reportNameMsgKey, messages, locale,ds);
        try {
            this.configuration = configuration;
            this.po = po;
            this.itemEntityList = itemEntityList;
            this.preamble = preamble;
            this.clausesList = clausesList;
            this.cashflowEntity = cashflowEntity;
            this.entityManager = entityManager;
            this.draft = draft;
            this.poDocumentList = poDocumentList;
            //TODO Va al Schedule
            addParameters("patternDecimal", configuration.getPatternDecimal());
            addParameters("FORMAT_DATE", configuration.getFormatDate());
            //TODO Va al Schedule
            addParameters("FORMAT_DATE2", configuration.getHardFormatDate());
            //TODO Va al Schedule
            addParameters("TIME_ZONE", configuration.getTimeZone());
            //TODO Va al Schedule
            addParameters("SUBREPORT_DIR", "reports/procurement/printPo/");

            loadParamPurchaseOrder();
            addParameters("paymentTerm", cashflowEntity != null && cashflowEntity.getPaymentTerms() != null ? cashflowEntity.getPaymentTerms().getLabel().toUpperCase() : null);
            addParameters("retentionApplicable", cashflowEntity != null && cashflowEntity.getApplyRetention() != null ? BooleanUtils.toStringYesNo(cashflowEntity.getApplyRetention()).toUpperCase() : "NO");
            addParameters("retentionForm", cashflowEntity != null && cashflowEntity.getForm() != null ? cashflowEntity.getForm().toUpperCase() : null);
            addParameters("securityDeposit", cashflowEntity != null && cashflowEntity.getApplyRetentionSecurityDeposit() != null ? BooleanUtils.toStringYesNo(cashflowEntity.getApplyRetentionSecurityDeposit()).toUpperCase() : "NO");
            addParameters("securityDepositForm", cashflowEntity != null && cashflowEntity.getFormSecurityDeposit() != null ? cashflowEntity.getFormSecurityDeposit().toUpperCase() : null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadParamPurchaseOrder() {
        resourceUtils = new ResourceUtils();
        addParameters("purchaseOrderId", po.getId());
        String variation = generateVariation(po.getVariation());
        //TODO Va a schedule
        addParameters("variation", variation != null ? "v" + variation : variation);
        //TODO Va a schedule
        addParameters("poNo", po.getPo());
        //TODO CAMBIAR EL PARAMETRO DE poId a PO_ID, va a schedule
        addParameters("PO_ID", po.getPurchaseOrderProcurementEntity().getId());

        //TODO Va a schedule
        if (po.getPurchaseOrderProcurementEntity().getClazz() != null) {
            if (po.getPurchaseOrderProcurementEntity().getClazz().ordinal() == ClassEnum.PO.ordinal() || po.getPurchaseOrderProcurementEntity().getClazz().ordinal() == ClassEnum.MINING_FLEET.ordinal()) {
                addParameters("titleReport", "Purchase Order");
            } else {
                addParameters("titleReport", "Contract");
            }
        } else {
            addParameters("titleReport", "Purchase Order");
        }
        //--------------------------------

        addParameters("poList", createDataSource(getPOReportDto()));

        //TODO Va a Schedule
        addParameters("projectNumber", po.getProjectEntity().getProjectNumber());
        //TODO va a schedule
        int i = 1;
        Map<Long, String> currencies = getCurrenciesForPayment();
        for (Long key : currencies.keySet()) {
            if (i <= 3) {
                addParameters("currencyLbl" + i, currencies.get(key));
                addParameters("currencyId" + i, key);
            } else
                break;
            i++;
        }
        //----------------------

        //TODO va a schedule
        addParameters("currenciesNumber", currencies.size());
        //TODO va a schedule
        String doc = poDocumentList.isEmpty() ? "" : poDocumentList.get(0).getDescription();
        addParameters("docs", doc);
    }

    private String generateVariation(String variation) {
        String variationNumber = Util.removePrefixForVariation(variation, "v");
        return variationNumber != null && variationNumber.trim().length() > 0 && !variationNumber.trim().equalsIgnoreCase("0") ? variationNumber.trim() : null;
    }


    private Map<Long, String> getCurrenciesForPayment() {
        Map<Long, String> currencies = new HashMap<Long, String>();
        if (cashflowEntity != null) {
            for (CashflowDetailEntity cashflowDetailEntity : cashflowEntity.getCashflowDetailList()) {
                if (cashflowDetailEntity.getProjectCurrency() != null) {
                    currencies.put(cashflowDetailEntity.getProjectCurrency().getId(), cashflowDetailEntity.getProjectCurrency().getCurrency().getSymbol() != null ? cashflowDetailEntity.getProjectCurrency().getCurrency().getSymbol() : cashflowDetailEntity.getProjectCurrency().getCurrency().getCode());
                }
            }
        }
        return currencies;
    }

    private List<PurchaseOrderReportDto> getPOReportDto() {
        List<PurchaseOrderReportDto> dtos = new ArrayList<>();
        dtos.add(new PurchaseOrderReportDto(po.getPoTitle()));
        if (StringUtils.isNotEmpty(preamble)) {
            dtos.add(new PurchaseOrderReportDto(null, this.preamble));
        }
        for (ItemEntity entity : this.itemEntityList) {
            PurchaseOrderReportDto dto = new PurchaseOrderReportDto(entity);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void printDocument(Long documentId) {

    }
}

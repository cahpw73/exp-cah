package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.processor.Processor;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ClassEnum;
import ch.swissbytes.domain.types.StatusEnum;
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
import org.xml.sax.SAXParseException;

import javax.annotation.Resource;
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
    public class ReportPurchaseOrder extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportPurchaseOrder.class.getName());
    // private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
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
    private Date startDate;
    private Date endDate;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                               Configuration configuration, PurchaseOrderEntity po, List<ItemEntity> itemEntityList, DataSource dataSource,
                               String preamble, List<ClausesEntity> clausesList, CashflowEntity cashflowEntity, EntityManager entityManager, boolean draft, List<PODocumentEntity> poDocumentList) throws IOException, DocumentException {
        super(filenameJasper, reportNameMsgKey, messages, locale,dataSource);
        startDate = new Date();
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
            addParameters("patternDecimal", configuration.getPatternDecimal());
            addParameters("FORMAT_DATE", configuration.getFormatDate());
            addParameters("FORMAT_DATE2", configuration.getHardFormatDate());
            addParameters("TIME_ZONE", configuration.getTimeZone());
            addParameters("SUBREPORT_DIR", "reports/procurement/printPo/");
            loadParamPurchaseOrder();
            addParameters("paymentTerm", cashflowEntity != null && cashflowEntity.getPaymentTerms() != null ? cashflowEntity.getPaymentTerms().getLabel().toUpperCase() : null);
            addParameters("retentionApplicable", cashflowEntity != null && cashflowEntity.getApplyRetention() != null ? BooleanUtils.toStringYesNo(cashflowEntity.getApplyRetention()).toUpperCase() : "NO");
            addParameters("retentionForm", cashflowEntity != null && cashflowEntity.getForm() != null ? cashflowEntity.getForm().toUpperCase() : null);
            addParameters("securityDeposit", cashflowEntity != null && cashflowEntity.getApplyRetentionSecurityDeposit() != null ? BooleanUtils.toStringYesNo(cashflowEntity.getApplyRetentionSecurityDeposit()).toUpperCase() : "NO");
            addParameters("securityDepositForm", cashflowEntity != null && cashflowEntity.getFormSecurityDeposit() != null ? cashflowEntity.getFormSecurityDeposit().toUpperCase() : null);
            loadOtherDocumentList(dataSource);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadParamPurchaseOrder() {
        resourceUtils = new ResourceUtils();
        loadParamLogos();
        addParameters("purchaseOrderId", po.getId());
        String variation = generateVariation(po.getVariation());
        addParameters("variation", variation != null ? "v" + variation : variation);
        addParameters("titleVariation", variation != null ? variation : variation);
        loadParamSupplier();
        if (po.getProjectEntity().getClient() != null) {
            addParameters("clientName", po.getProjectEntity().getClient().getName().trim());
        }
        Processor processor = new Processor(true);
        processor.useArial();
        addParameters("poNo", po.getPo());
        addParameters("poId", po.getPurchaseOrderProcurementEntity().getId());
        addParameters("orderDate", po.getPurchaseOrderProcurementEntity().getOrderDate());
        addParameters("deliveryDate", po.getPoDeliveryDate());
        addParameters("deliveryDateStr", convertDeliveryDate());
        addParameters("deliveryPoint", po.getPurchaseOrderProcurementEntity().getPoint() != null ? po.getPurchaseOrderProcurementEntity().getPoint().toUpperCase() : null);
        addParameters("deliveryInstructions", po.getPurchaseOrderProcurementEntity().getDeliveryInstruction());
        addParameters("procManager", po.getPurchaseOrderProcurementEntity().getProcManager());
        addParameters("procManagerDetail", po.getPurchaseOrderProcurementEntity().getProcManagerDetail());
        if (draft) {
            InputStream watermark = resourceUtils.getResourceAsStream("/images/draft-report.jpg");
            log.info("InputStream watermark: " + watermark.toString());
            addParameters("watermarkDraft", watermark);
        }
        if (po.getOrderedVariation().intValue() == 1) {
            addParameters("isOriginal", true);
        } else if (po.getOrderedVariation().intValue() > 1) {
            addParameters("poSummaryList", createDataSource(getPOSummary()));
        }
        if (po.getPurchaseOrderProcurementEntity().getClazz() != null) {
            if (po.getPurchaseOrderProcurementEntity().getClazz().ordinal() == ClassEnum.PO.ordinal() || po.getPurchaseOrderProcurementEntity().getClazz().ordinal() == ClassEnum.MINING_FLEET.ordinal()) {
                addParameters("titleReport", "Purchase Order");
            } else {
                addParameters("titleReport", "Contract");
            }
        } else {
            addParameters("titleReport", "Purchase Order");
        }

        addParameters("poList", createDataSource(getPOReportDto()));
        addParameters("totalClauses", this.clausesList.size());
        addParameters("poTitle", po.getPoTitle());
        addParameters("projectName", po.getProjectEntity().getTitle().toUpperCase());
        addParameters("projectNumber", po.getProjectEntity().getProjectNumber());

        addParameters("liquidatedDamagesApplicable", po.getPurchaseOrderProcurementEntity().getLiquidatedDamagesApplicable() != null ? BooleanUtils.toStringYesNo(po.getPurchaseOrderProcurementEntity().getLiquidatedDamagesApplicable()).toUpperCase() : null);
        addParameters("vendorDrawingData", po.getPurchaseOrderProcurementEntity().getVendorDrawingData() != null ? BooleanUtils.toStringYesNo(po.getPurchaseOrderProcurementEntity().getVendorDrawingData()).toUpperCase() : null);
        addParameters("exchangeRateVariation", po.getPurchaseOrderProcurementEntity().getExchangeRateVariation() != null ? BooleanUtils.toStringYesNo(po.getPurchaseOrderProcurementEntity().getExchangeRateVariation()).toUpperCase() : null);
        addParameters("rtfNo", collectRTFNo());
        addParameters("mrNo", collectMRNo());
        processor.clear();
        addParameters("invoiceTo", Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(po.getProjectEntity().getInvoiceTo())));
        if (po.getPurchaseOrderProcurementEntity().getContactEntity() != null) {
            addParameters("contactName", po.getPurchaseOrderProcurementEntity().getContactEntity().getFullName());
            addParameters("contactEmail", po.getPurchaseOrderProcurementEntity().getContactEntity().getEmail());
            if (StringUtils.isNotEmpty(po.getPurchaseOrderProcurementEntity().getContactEntity().getPhone()))
                addParameters("contactPhone", po.getPurchaseOrderProcurementEntity().getContactEntity().getPhone());
            else
                addParameters("contactPhone", po.getPurchaseOrderProcurementEntity().getSupplier().getPhone());
            if (StringUtils.isNotEmpty(po.getPurchaseOrderProcurementEntity().getContactEntity().getFax()))
                addParameters("contactFax", po.getPurchaseOrderProcurementEntity().getContactEntity().getFax());
            else
                addParameters("contactFax", po.getPurchaseOrderProcurementEntity().getSupplier().getFax());
        }
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
        Date now = new Date();
        addParameters("currenciesNumber", currencies.size());
        addParameters("currentDate", Util.convertUTC(now, configuration.getTimeZone()));
        addParameters("bigLogo", po.getProjectEntity().getClient().getBigImage() != null ? po.getProjectEntity().getClient().getBigImage() : false);
        addParameters("showClientName", po.getProjectEntity().getClient().getShowTitle() != null ? po.getProjectEntity().getClient().getShowTitle() : false);
        String doc = poDocumentList.isEmpty() ? "" : poDocumentList.get(0).getDescription();
        addParameters("docs", doc);
    }

    private String convertDeliveryDate() {
        String converted = "";
        if (po.getPoDeliveryDate() != null) {
            DateTimeZone dtz = org.joda.time.DateTimeZone.forID(configuration.getTimeZone());
            long utc = dtz.convertUTCToLocal(po.getPoDeliveryDate().getTime());
            Date date = new Date();
            date.setTime(utc);
            converted = new java.text.SimpleDateFormat(configuration.getHardFormatDate(), new Locale("en")).format(date).toUpperCase();
        }
        return converted;
    }

    private void loadParamLogos() {
        ImageUtil imageUtil = new ImageUtil();
        if (po.getProjectEntity().getClient() != null && po.getProjectEntity().getClient().getHeaderLogo() != null) {
            if (imageUtil.hasDimensionHeaderLogoCorrect(po.getProjectEntity().getClient().getHeaderLogo().getFile())) {
                InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getHeaderLogo().getFile());
                addParameters("headerLogo", logo);
            } else {
                BufferedImage newHeaderLogo = imageUtil.getNewImageResized(po.getProjectEntity().getClient().getHeaderLogo().getFile());
                InputStream logo = imageUtil.convertBufferedImageToInputStream(newHeaderLogo);
                addParameters("headerLogo", logo);
            }

        }
        if (po.getProjectEntity().getClient() != null && po.getProjectEntity().getClient().getClientLogo() != null) {
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientLogo().getFile());
            addParameters("logo", logo);
        }
    }

    private void loadParamSupplier() {
        if (po.getPurchaseOrderProcurementEntity().getSupplier() != null) {
            addParameters("company", po.getPurchaseOrderProcurementEntity().getSupplier().getCompany());
            String street = po.getPurchaseOrderProcurementEntity().getSupplier().getStreet();
            try {
                byte[] b = street.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            addParameters("street", po.getPurchaseOrderProcurementEntity().getSupplier().getStreet());
            addParameters("state", po.getPurchaseOrderProcurementEntity().getSupplier().getState());
            addParameters("suburb", po.getPurchaseOrderProcurementEntity().getSupplier().getSuburb());
            addParameters("abnReg", po.getPurchaseOrderProcurementEntity().getSupplier().getAbnRegNo());
            addParameters("postcode", po.getPurchaseOrderProcurementEntity().getSupplier().getPostCode());
            addParameters("country", po.getPurchaseOrderProcurementEntity().getSupplier().getCountry());
            addParameters("phone", po.getPurchaseOrderProcurementEntity().getSupplier().getPhone());
            addParameters("fax", po.getPurchaseOrderProcurementEntity().getSupplier().getFax());
            addParameters("supplierId", po.getPurchaseOrderProcurementEntity().getSupplier().getId());
            addParameters("townPostcodeState", getTownPostCodeStateParameter());
        }
    }

    private String getTownPostCodeStateParameter() {
        StringBuilder sb = new StringBuilder();
        boolean hasPrevious = false;
        if (StringUtils.isNotEmpty(po.getPurchaseOrderProcurementEntity().getSupplier().getSuburb()) && StringUtils.isNotBlank(po.getPurchaseOrderProcurementEntity().getSupplier().getSuburb())) {
            sb.append(po.getPurchaseOrderProcurementEntity().getSupplier().getSuburb());
            hasPrevious = true;
        }
        if (StringUtils.isNotEmpty(po.getPurchaseOrderProcurementEntity().getSupplier().getPostCode()) && StringUtils.isNotBlank(po.getPurchaseOrderProcurementEntity().getSupplier().getPostCode())) {
            if (hasPrevious) {
                sb.append(", ");
            }
            sb.append(po.getPurchaseOrderProcurementEntity().getSupplier().getPostCode());
            hasPrevious = true;
        }
        if (StringUtils.isNotEmpty(po.getPurchaseOrderProcurementEntity().getSupplier().getState()) && StringUtils.isNotBlank(po.getPurchaseOrderProcurementEntity().getSupplier().getState())) {
            if (hasPrevious) {
                sb.append(", ");
            }
            sb.append(po.getPurchaseOrderProcurementEntity().getSupplier().getState());
        }
        return sb.toString();
    }

    private void loadParamClients() {
        Processor processor = new Processor(true);
        processor.useArial();
        if (po.getProjectEntity().getClient() != null) {
            addParameters("clientName", po.getProjectEntity().getClient().getName().trim());
            Map detail = separateDetail(po.getProjectEntity().getClient().getTitle());
            addParameters("clientDetail0", detail.size() > 0 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(0).toString())) : null);
            processor.clear();
            addParameters("clientDetail1", detail.size() > 1 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(1).toString())) : null);
            processor.clear();
            addParameters("clientDetail2", detail.size() > 2 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(2).toString())) : null);
            processor.clear();
            addParameters("clientDetail3", detail.size() > 3 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(3).toString())) : null);
        }
    }

    private String collectMRNo() {
        StringBuilder sb = new StringBuilder();
        for (RequisitionEntity requisitionEntity : po.getPurchaseOrderProcurementEntity().getRequisitions()) {
            sb.append(requisitionEntity.getRequisitionNumber());
            sb.append(",");
        }
        Integer limit = sb.toString().length();
        if (sb.toString().length() > 0) {
            limit = limit - 1;
        }
        return sb.toString().substring(0, limit);
    }

    private String collectRTFNo() {
        StringBuilder sb = new StringBuilder();
        for (RequisitionEntity requisitionEntity : po.getPurchaseOrderProcurementEntity().getRequisitions()) {
            if (StringUtils.isNotEmpty(requisitionEntity.getrTFNo()) && StringUtils.isNotBlank(requisitionEntity.getrTFNo())) {
                sb.append(requisitionEntity.getrTFNo());
            } else if (StringUtils.isNotEmpty(requisitionEntity.getOriginator()) && StringUtils.isNotBlank(requisitionEntity.getOriginator())) {
                sb.append(requisitionEntity.getOriginator());
            }
            sb.append(",");
        }
        Integer limit = sb.toString().length();
        if (sb.toString().length() > 0) {
            limit = limit - 1;
        }
        return sb.toString().substring(0, limit);
    }

    private String generateVariation(String variation) {
        String variationNumber = Util.removePrefixForVariation(variation, "v");
        return variationNumber != null && variationNumber.trim().length() > 0 && !variationNumber.trim().equalsIgnoreCase("0") ? variationNumber.trim() : null;
    }

    private Map<Integer, String> separateDetail(String detail) {
        Map<Integer, String> columns = new HashMap<>();
        String tableOpen = "<table>";
        String tableClose = "</table>";
        int index = detail.toLowerCase().indexOf(tableOpen);
        String separator = System.lineSeparator() + System.lineSeparator();
        int i = 0;
        String textToProcess = detail;
        if (index >= 0) {
            String firstText = StringUtils.chomp(detail.substring(0, index));
            String remainingText = StringUtils.chomp(detail.substring(index + tableOpen.length(), detail.length()));
            columns.put(i, firstText);
            i++;
            remainingText = remainingText.replaceAll(tableClose, "");
            remainingText = remainingText.replaceAll(tableOpen, "");
            textToProcess = remainingText;
            for (String column : textToProcess.split(separator)) {
                while (column.startsWith(System.lineSeparator()) && column.length() > 0) {
                    column = column.substring(1, column.length());
                }
                if (StringUtils.isNotEmpty(column) && StringUtils.isNotBlank(column)) {
                    columns.put(i, StringUtils.chomp(column));
                    i++;
                }
            }
        } else {
            columns.put(i, detail);
        }

        return columns;
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

    private List<ClausesReportDto> getClausesReportDto() {
        List<ClausesReportDto> dtos = new ArrayList<>();
        for (ClausesEntity entity : clausesList) {
            ClausesReportDto dto = new ClausesReportDto(entity);
            dtos.add(dto);
        }
        return dtos;
    }

    private List<PurchaseOrderSummaryDto> getPOSummary() {
        log.info("preparing poSummary dto.......");
        List<PurchaseOrderSummaryDto> dtos = new ArrayList<>();
        List<Object> list = getSummaryItemsPO();
        currenciesUsedSummaryPO();
        if (po.getOrderedVariation().intValue() == 2) {
            log.info("Po is the first variation");
            addParameters("isOriginal", false);
            loadPOSummaryOriginal(list, dtos);
            loadThisVariation(list, dtos);
            loadRevisedOrderValue(list, dtos);
        } else if (po.getOrderedVariation().intValue() >= 3) {
            log.info("Po is the variation N");
            addParameters("isOriginal", false);
            loadPOSummaryOriginal(list, dtos);
            loadBetweenVariation(list, dtos);
            loadThisVariation(list, dtos);
            loadRevisedOrderValue(list, dtos);
        }

        return dtos;
    }

    private void loadBetweenVariation(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        List<Object> listAux = new ArrayList<>();
        listAux.addAll(list);
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitle("Variation No. " + getDesriptionBetweenVariation());
        boolean total1HasChanges = false;
        boolean total2HasChanges = false;
        boolean total3HasChanges = false;
        for (Object record : list) {
            Object[] values = (Object[]) record;
            if (currenciesIdsPo[0] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[0]).longValue()) {
                BigInteger currencyId = (BigInteger) values[0];
                BigDecimal total1 = new BigDecimal(0);
                for (Object recordT1 : list) {
                    Object[] valuesT1 = (Object[]) recordT1;
                    if ((int) valuesT1[3] > 1 && (int) valuesT1[3] < po.getOrderedVariation().intValue()) {
                        if (currencyId.intValue() == ((BigInteger) valuesT1[0]).intValue()) {
                            total1 = total1.add((BigDecimal) valuesT1[4]);
                            total1HasChanges = true;
                        }
                    }
                }
                if (total1HasChanges) {
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode1((String) values[2]);
                    } else {
                        dto.setCurrencyCode1((String) values[1]);
                    }
                    dto.setAmount1(total1);
                }
            }
            if (currenciesIdsPo[1] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[1]).longValue()) {
                BigInteger currencyId = (BigInteger) values[0];
                BigDecimal total2 = new BigDecimal(0);
                for (Object recordT1 : list) {
                    Object[] valuesT1 = (Object[]) recordT1;
                    if ((int) valuesT1[3] > 1 && (int) valuesT1[3] < po.getOrderedVariation().intValue()) {
                        if (currencyId.intValue() == ((BigInteger) valuesT1[0]).intValue()) {
                            total2 = total2.add((BigDecimal) valuesT1[4]);
                            total2HasChanges = true;
                        }
                    }
                }
                if (total2HasChanges) {
                    dto.setPlus1("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode2((String) values[2]);
                    } else {
                        dto.setCurrencyCode2((String) values[1]);
                    }
                    dto.setAmount2(total2);
                }
            }
            if (currenciesIdsPo[2] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[2]).longValue()) {
                BigInteger currencyId = (BigInteger) values[0];
                BigDecimal total3 = new BigDecimal(0);
                for (Object recordT1 : list) {
                    Object[] valuesT1 = (Object[]) recordT1;
                    if ((int) valuesT1[3] > 1 && (int) valuesT1[3] < po.getOrderedVariation().intValue()) {
                        if (currencyId.intValue() == ((BigInteger) valuesT1[0]).intValue()) {
                            total3 = total3.add((BigDecimal) valuesT1[4]);
                            total3HasChanges = true;
                        }
                    }
                }
                if (total3HasChanges) {
                    dto.setPlus2("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode3((String) values[2]);
                    } else {
                        dto.setCurrencyCode3((String) values[1]);
                    }
                    dto.setAmount3(total3);
                }
            }
        }
        dtos.add(dto);
    }

    private void loadRevisedOrderValue(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        List<Object> listAux = new ArrayList<>();
        listAux.addAll(list);
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitleTotal("Revised Order Value");

        String code1 = "";
        BigDecimal amt1 = new BigDecimal(0);
        boolean flagAmt1 = false;
        String code2 = "";
        BigDecimal amt2 = new BigDecimal(0);
        boolean flagAmt2 = false;
        String code3 = "";
        BigDecimal amt3 = new BigDecimal(0);
        boolean flagAmt3 = false;
        for (PurchaseOrderSummaryDto d : dtos) {
            if (d.getAmount1() != null) {
                flagAmt1 = true;
                amt1 = amt1.add(d.getAmount1());
            }
            if (d.getAmount2() != null) {
                dto.setPlusTotal1("plus");
                flagAmt2 = true;
                amt2 = amt2.add(d.getAmount2());
            }
            if (d.getAmount3() != null) {
                dto.setPlusTotal2("plus");
                flagAmt3 = true;
                amt3 = amt3.add(d.getAmount3());
            }
            if (StringUtils.isNotEmpty(d.getCurrencyCode1())) {
                code1 = d.getCurrencyCode1();
            }
            if (StringUtils.isNotEmpty(d.getCurrencyCode2())) {
                code2 = d.getCurrencyCode2();
            }
            if (StringUtils.isNotEmpty(d.getCurrencyCode3())) {
                code3 = d.getCurrencyCode3();
            }
        }
        dto.setCurrencyCodeTotal1(code1);
        dto.setCurrencyCodeTotal2(code2);
        dto.setCurrencyCodeTotal3(code3);
        dto.setAmountTotal1(flagAmt1 ? amt1 : null);
        dto.setAmountTotal2(flagAmt2 ? amt2 : null);
        dto.setAmountTotal3(flagAmt3 ? amt3 : null);
        dtos.add(dto);
    }

    private void loadThisVariation(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitle("This Variation No." + po.getVariation());

        for (Object record : list) {
            Object[] values = (Object[]) record;
            if ((int) values[3] == po.getOrderedVariation()) {
                if (currenciesIdsPo[0] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[0]).longValue()) {
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode1((String) values[2]);
                    } else {
                        dto.setCurrencyCode1((String) values[1]);
                    }
                    dto.setAmount1((BigDecimal) values[4]);
                }
                if (currenciesIdsPo[1] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[1]).longValue()) {
                    dto.setPlus1("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode2((String) values[2]);
                    } else {
                        dto.setCurrencyCode2((String) values[1]);
                    }
                    dto.setAmount2((BigDecimal) values[4]);
                }
                if (currenciesIdsPo[2] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[2]).longValue()) {
                    dto.setPlus2("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode3((String) values[2]);
                    } else {
                        dto.setCurrencyCode3((String) values[1]);
                    }
                    dto.setAmount3((BigDecimal) values[4]);
                }
            }
        }
        dtos.add(dto);
    }

    private void loadPOSummaryOriginal(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitle("Original Order Value");

        for (Object record : list) {
            Object[] values = (Object[]) record;
            if ((int) values[3] == 1) {
                if (currenciesIdsPo[0] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[0]).longValue()) {
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode1((String) values[2]);
                    } else {
                        dto.setCurrencyCode1((String) values[1]);
                    }
                    dto.setAmount1((BigDecimal) values[4]);
                    currenciesIdsPo[0] = (BigInteger) values[0];
                }
                if (currenciesIdsPo[1] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[1]).longValue()) {
                    dto.setPlus1("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode2((String) values[2]);
                    } else {
                        dto.setCurrencyCode2((String) values[1]);
                    }
                    dto.setAmount2((BigDecimal) values[4]);
                }
                if (currenciesIdsPo[2] != null && ((BigInteger) values[0]).longValue() == (currenciesIdsPo[2]).longValue()) {
                    dto.setPlus2("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode3((String) values[2]);
                    } else {
                        dto.setCurrencyCode3((String) values[1]);
                    }
                    dto.setAmount3((BigDecimal) values[4]);
                }
            }
        }
        dtos.add(dto);
    }

    private List<Object> getSummaryItemsPO() {
        Query query = entityManager.createNativeQuery("select cu.id,cu.code,cu.symbol,po.orderedvariation,sum (total_cost),po.variation\n" +
                "from item sp left outer join purchase_order po  on sp.purchase_order_id= po.id\n" +
                "left outer join project_currency pc on pc.id= sp.project_currency_id\n" +
                "left outer join currency cu on pc.currency_id=cu.id\n" +
                "where sp.status_id = 1 and po.status_id = 1 and (cu.id is not null or cu.code is not null or cu.symbol is not null) " +
                "and po.po = '" + po.getPo() + "' and po.project_id=" + po.getProjectEntity().getId() + "\n" +
                "group by cu.id,cu.code,cu.symbol,po.orderedvariation,po.variation\n" +
                "order by po.orderedvariation");
        return query.getResultList();
    }

    private void currenciesUsedSummaryPO() {
        Query query = entityManager.createNativeQuery("select cu.id\n" +
                "from item sp inner join purchase_order po  on sp.purchase_order_id= po.id\n" +
                "left join project_currency pc on pc.id= sp.project_currency_id\n" +
                "inner join currency cu on pc.currency_id=cu.id\n" +
                "where sp.status_id=1 and po.status_id = 1 and po.po = '" + po.getPo() + "' and po.project_id= " + po.getProjectEntity().getId() + " and orderedvariation between " + 1 + " and " + po.getOrderedVariation() + "\n" +
                "group by orderedvariation,cu.id\n" +
                "order by orderedvariation,cu.id");
        List<BigInteger> list = query.getResultList();
        List<BigInteger> orderedList = Collections.synchronizedList(new ArrayList<BigInteger>());
        for (BigInteger l : list) {
            if (orderedList.isEmpty()) {
                orderedList.add(l);
            } else {
                boolean hasCurrenciId = false;
                for (BigInteger o : orderedList) {
                    if (o.longValue() == l.longValue()) {
                        hasCurrenciId = true;
                        break;
                    }
                }
                if (!hasCurrenciId) {
                    orderedList.add(l);
                }
            }
        }
        int index = 0;
        currenciesIdsPo[0] = null;
        currenciesIdsPo[1] = null;
        currenciesIdsPo[2] = null;
        for (BigInteger record : orderedList) {
            if (index <= 2) {
                currenciesIdsPo[index] = record;
            } else {
                break;
            }
            index++;
        }
    }

    private String getDesriptionBetweenVariation() {
        Long poMinId = -1L;
        Long poMaxId = -1L;
        String result = "";
        Query query = entityManager.createNativeQuery("select distinct po.orderedvariation, po.id\n" +
                "from purchase_order po  \n" +
                "where po.po = '" + po.getPo() + "' and po.project_id= " + po.getProjectEntity().getId() + " and  po.status_id = 1 " + "\n" +
                "order by po.orderedvariation");
        List<Object> list = query.getResultList();
        for (Object record : list) {
            Object[] values = (Object[]) record;
            if ((int) values[0] == 2) {
                poMinId = ((BigInteger) values[1]).longValue();
            }
            if ((int) values[0] == po.getOrderedVariation() - 1) {
                poMaxId = ((BigInteger) values[1]).longValue();
            }
        }

        if (poMinId.longValue() != -1L && poMaxId.longValue() != -1L) {
            if (poMinId.longValue() != poMaxId.longValue()) {
                PurchaseOrderEntity poMin = findPurchaseOrderById(poMinId);
                PurchaseOrderEntity poMax = findPurchaseOrderById(poMaxId);
                result = poMin.getVariation() + " - " + poMax.getVariation();
            } else if (poMinId.longValue() == poMaxId.longValue()) {
                PurchaseOrderEntity poMin = findPurchaseOrderById(poMinId);
                result = poMin.getVariation();
            }
        }

        return result;
    }

    private PurchaseOrderEntity findPurchaseOrderById(final Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM PurchaseOrderEntity p ");
        sb.append(" WHERE p.id = :ID ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("ID", id);
        List<PurchaseOrderEntity> list = query.getResultList();
        return list.get(0);
    }


    private void loadOtherDocumentList(DataSource dataSource) throws IOException, DocumentException {
        log.info("Loading others documents");
        Date startOtherDocuments = new Date();
        if (!poDocumentList.isEmpty()) {
            for (PODocumentEntity pd : poDocumentList) {
                if (pd.getScheduleE() == null) {
                    if (StringUtils.isNotEmpty(pd.getDescription())) {
                        pd.setDescription(pd.getDescription().replaceAll("\\{po-title}", po.getPoTitle()));
                        pd.setDescription(pd.getDescription().replaceAll("\\{po-number}", po.getPo() + "v" + po.getVariation()));
                        otherDocumentList.add(getReportFromHtml(pd.getDescription()));
                    } else {
                        log.info("attachmentPoDocId = " + pd.getAttachmentProjectDocument().getId());
                        AttachmentMainDocumentEntity attachmentMainDocument = getAttachmentDocument(pd.getAttachmentProjectDocument().getId());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(attachmentMainDocument.getFile().length);
                        baos.write(attachmentMainDocument.getFile(), 0, attachmentMainDocument.getFile().length);
                        otherDocumentList.add(baos);
                    }
                } else {
                    otherDocumentList.add(getReportSchedule());
                }
            }
        } else {
            otherDocumentList.add(getReportSchedule());
        }
        Date endOtherDocuments = new Date();
        log.info("endOtherDocuments time - startOtherDocuments time = "+(endOtherDocuments.getTime()-startOtherDocuments.getTime())+"ms");
    }

    public AttachmentMainDocumentEntity getAttachmentDocument(final Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM AttachmentMainDocumentEntity x ");
        sb.append("WHERE x.id=:MAIN_DOC_ID ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("MAIN_DOC_ID", id);
        List<AttachmentMainDocumentEntity> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    private ByteArrayOutputStream getReportFromHtml(final String contentPdf) throws IOException, DocumentException {

        log.info("converting html to pdf");
        String titleHeader;
        if (po.getPurchaseOrderProcurementEntity().getClazz() != null) {
            if (po.getPurchaseOrderProcurementEntity().getClazz().ordinal() == ClassEnum.PO.ordinal() || po.getPurchaseOrderProcurementEntity().getClazz().ordinal() == ClassEnum.MINING_FLEET.ordinal()) {
                titleHeader = "PURCHASE ORDER" + " " + po.getProjectEntity().getProjectNumber() + "-" + po.getPo() + (po.getOrderedVariation() > 1 ? ("v" + po.getVariation()) : "");
            } else {
                titleHeader = "CONTRACT" + " " + po.getProjectEntity().getProjectNumber() + "-" + po.getPo() + (po.getOrderedVariation() > 1 ? ("v" + po.getVariation()) : "");
            }
        } else {
            titleHeader = "PURCHASE ORDER" + " " + po.getProjectEntity().getProjectNumber() + "-" + po.getPo() + (po.getOrderedVariation() > 1 ? ("v" + po.getVariation()) : "");
        }
        XmlWorker xmlWorker = new XmlWorker();
        return xmlWorker.convertHtml(contentPdf, titleHeader);
    }

    public ByteArrayOutputStream getReportSchedule() throws IOException, DocumentException {
        log.info("getting schedule report");
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        Map<String, String> messages = new HashMap<>();
        ReportView reportView = new ReportSheduleE("/procurement/printPo/Schedule", "procurement.schedule",
                messages, locale, configuration, po, itemEntityList, preamble, clausesList, cashflowEntity, entityManager, draft, poDocumentList,getDataSource());
        return reportView.getByteArrayOutputStreamReport();
    }

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport(null, otherDocumentList);
            endDate = new Date();
            log.info("end time - start time = " + (endDate.getTime() - startDate.getTime()) + "ms");
        } catch (Exception ex) {
            if (!(ex.getMessage().contains("'&'") && ex.getMessage().contains("org.xml.sax.SAXParseException;"))) {
                log.info("ex message contains SAXParseException;");
                ex.printStackTrace();
            } else {
                log.log(Level.WARNING, "A special character is being used [&]");
            }
        }
    }
}

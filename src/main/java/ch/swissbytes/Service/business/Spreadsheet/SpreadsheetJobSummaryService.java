package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LanguagePreference;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.HssfSpreadsheetProcessor;
import ch.swissbytes.procurement.util.SpreadsheetProcessor;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Christian on 21/07/2015.
 */
public class SpreadsheetJobSummaryService implements Serializable {

    public static final Logger log = Logger.getLogger(SpreadsheetJobSummaryService.class.getName());

    @Inject
    private ScopeSupplyDao supplyDao;
    @Inject
    private CashflowService cashflowService;
    @Inject
    private ScopeSupplyService scopeSupplyService;
    @Inject
    public HssfSpreadsheetProcessor processor;
    @Inject
    public LanguagePreference languagePreference;

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    public Configuration configuration = new Configuration();

    int rowNo;

    public void generateWorkbookToExport(final List<PurchaseOrderEntity> list, String folderName) throws Exception {
        rowNo = 2;
        String pathCMS = System.getProperty("fqmes.path.export.cms");
        pathCMS = pathCMS.replace("{project_field}", folderName);
        processWorkbook(list);
        log.info("written CMS successfully...");
        processor.doSaveWorkBook(pathCMS, generateFileName());
    }

    public InputStream generateWorkbook(final List<PurchaseOrderEntity> list) {
        rowNo = 3;
        processWorkbook(list);
        log.info("written successfully...");
        return processor.getContentSheet();
    }

    private String generateFileName() {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
        String dateStr = format.format(new Date());
        String fileName = dateStr.toUpperCase() + " - " + "Commitments.xls";
        return fileName;
    }

    public void processWorkbook(final List<PurchaseOrderEntity> list) {
        processor = new HssfSpreadsheetProcessor();
        processor.createWorkbook();
        processor.createSpreadsheetWithoutPassword("PkgHdr");
        prepareWithColumns();
        createHeaderJS();
        createHeaderPO();
        generateSpreadsheetPurchaseOrderDetail(list);
    }

    private void prepareWithColumns() {
        processor.configureWithColumn(0, 15000);
        processor.configureWithColumn(1, 3000);
        processor.configureWithColumn(3, 11000);
        processor.configureWithColumn(4, 4000);
        processor.configureWithColumn(5, 4000);
        processor.configureWithColumn(6, 5000);
        processor.configureWithColumn(7, 5000);
        processor.configureWithColumn(8, 5000);
        processor.configureWithColumn(9, 3000);
        processor.configureWithColumn(10, 5000);
        processor.configureWithColumn(11, 5800);
        processor.configureWithColumn(12, 5000);
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list) {
        int nexIndex = 0;
        for (PurchaseOrderEntity entity : list) {
            List<ScopeSupplyEntity> scopeSupplyListList = scopeSupplyService.scopeSupplyListByPOId(entity.getId());
            if (hasScopeSupplyExcludeFromExpediting(scopeSupplyListList)) {
                Map<String, Integer> indexRichString = new HashMap<>();
                processor.createRow(rowNo);
                String poH = "PO: " + entity.getProject() + " " + entity.getPo() + " v" + entity.getVariation() + ", ";
                indexRichString.put("poIni", 0);
                indexRichString.put("poEnd", 4);
                nexIndex = poH.length();

                String titleH = "Title: " + (StringUtils.isNotEmpty(entity.getExpeditingTitle()) ? entity.getExpeditingTitle() + ", " : (StringUtils.isNotEmpty(entity.getPoTitle()) ? entity.getPoTitle() + ", " : ", "));
                indexRichString.put("titleIni", nexIndex);
                indexRichString.put("titleEnd", nexIndex + 7);
                String aux = poH + titleH;
                nexIndex = aux.length();

                String deliveryDateH = "PO Del. Date: " + (entity.getPoDeliveryDate() != null ? Util.toLocal(entity.getPoDeliveryDate(), languagePreference.getTimeZone(), "MMM, dd yyyy") + ", " : ", ");
                indexRichString.put("dateIni", nexIndex);
                indexRichString.put("dateEnd", nexIndex + 14);
                aux = aux + deliveryDateH;
                nexIndex = aux.length();

                String incoTermH = "INCO Term: " + entity.getIncoTerm() + " " + entity.getFullIncoTerms() + ", ";
                indexRichString.put("incoIni", nexIndex);
                indexRichString.put("incoEnd", nexIndex + 11);
                aux = aux + incoTermH;
                nexIndex = aux.length();

                String supplierH = "Supplier: " + entity.getPurchaseOrderProcurementEntity().getSupplier().getCompany() + ", ";
                indexRichString.put("supIni", nexIndex);
                indexRichString.put("supEnd", nexIndex + 10);
                aux = aux + supplierH;
                nexIndex = aux.length();

                String statusH = "Status: " + "[" + entity.getPurchaseOrderStatus().getLabel().toUpperCase() + "], ";
                indexRichString.put("statusIni", nexIndex);
                indexRichString.put("statusEnd", nexIndex + 8);
                aux = aux + statusH;
                nexIndex = aux.length();

                String ref = "RfE: " + entity.getResponsibleExpediting();
                indexRichString.put("rfeIni", nexIndex);
                indexRichString.put("rfeEnd", nexIndex + 5);

                String poTitleReport = poH + titleH + deliveryDateH + incoTermH + supplierH + statusH + ref;
                processor.writeRichStringValue(0, poTitleReport,indexRichString);
                //processor.writeStringValue(0, poTitleReport);
                rowNo++;

                if (!scopeSupplyListList.isEmpty()) {
                    for (ScopeSupplyEntity ss : scopeSupplyListList) {
                        if (ss.getExcludeFromExpediting() == null || !ss.getExcludeFromExpediting()) {
                            processor.createRow(rowNo);
                            prepareDetailContent(ss);
                            rowNo++;
                        }
                    }
                }
            }
        }
    }

    private boolean hasScopeSupplyExcludeFromExpediting(List<ScopeSupplyEntity> list) {
        for (ScopeSupplyEntity ss : list) {
            if (ss.getExcludeFromExpediting() == null || !ss.getExcludeFromExpediting().booleanValue()) {
                return true;
            }
        }
        return false;
    }

    private void prepareDetailContent(ScopeSupplyEntity ss) {
        DecimalFormat decFormat = new DecimalFormat(new Configuration().getPatternDecimal());
        processor.writeStringValue(0, StringUtils.isNotEmpty(ss.getCode()) ? ss.getCode() : "");
        processor.writeStringValue(1, ss.getQuantity() != null ? decFormat.format(ss.getQuantity()) : "");
        processor.writeStringValue(2, StringUtils.isNotEmpty(ss.getUnit()) ? ss.getUnit() : "");
        processor.writeStringValue(3, StringUtils.isNotEmpty(ss.getDescription()) ? ss.getDescription() : "");
        processor.writeStringValue(4, StringUtils.isNotEmpty(ss.getTagNo()) ? ss.getTagNo() : "");
        processor.writeStringValue(5, StringUtils.isNotEmpty(ss.getSpIncoTermDescription()) ? ss.getSpIncoTermDescription() : "");
        processor.writeStringValue(6, ss.getPoDeliveryDate() != null ? Util.toLocal(ss.getPoDeliveryDate(), languagePreference.getTimeZone(), configuration.getFormatDate()) : "");
        processor.writeStringValue(7, ss.getForecastExWorkDate() != null ? Util.toLocal(ss.getForecastExWorkDate(), languagePreference.getTimeZone(), configuration.getFormatDate()) : "");
        processor.writeStringValue(8, ss.getActualExWorkDate() != null ? Util.toLocal(ss.getActualExWorkDate(), languagePreference.getTimeZone(), configuration.getFormatDate()) : "");
        String deliveryQt = ss.getDeliveryLeadTimeQt() != null ? String.valueOf(ss.getDeliveryLeadTimeQt().intValue()) : "";
        String deliveryMt = ss.getDeliveryLeadTimeMs() != null ? bundle.getString("measurement.time." + ss.getDeliveryLeadTimeMs().name().toLowerCase()) : "";
        processor.writeStringValue(9, deliveryQt + " " + deliveryMt);
        processor.writeStringValue(10, ss.getForecastSiteDate() != null ? Util.toLocal(ss.getForecastSiteDate(), languagePreference.getTimeZone(), configuration.getFormatDate()) : "");
        processor.writeStringValue(11, ss.getActualSiteDate() != null ? Util.toLocal(ss.getActualSiteDate(), languagePreference.getTimeZone(), configuration.getFormatDate()) : "");
        processor.writeStringValue(12, ss.getRequiredSiteDate() != null ? Util.toLocal(ss.getRequiredSiteDate(), languagePreference.getTimeZone(), configuration.getFormatDate()) : "");
        Integer dateDiff = datePart(ss.getRequiredSiteDate(), ss.getForecastSiteDate());
        processor.writeStringValue(13, dateDiff != null ? String.valueOf(dateDiff.intValue()) : "");
    }

    private void createHeaderJS() {
        processor.createRow(0);
        processor.writeStringBoldValue(0, "Job Summary Report");
        processor.writeStringBoldValue(1, Util.toLocal(new Date(), languagePreference.getTimeZone(), "MMM, dd yyyy"));
        processor.createRow(1);
    }

    private void createHeaderPO() {
        processor.createRow(2);
        processor.writeStringValue(0, "Item No");
        processor.writeStringValue(1, "Qty");
        processor.writeStringValue(2, "Unit");
        processor.writeStringValue(3, "Item Description");
        processor.writeStringValue(4, "Equipment Tag");
        processor.writeStringValue(5, "Full Inco Term");
        processor.writeStringValue(6, "PO Delivery Date");
        processor.writeStringValue(7, "Forecast Ex Works Date");
        processor.writeStringValue(8, "Actual Ex Works");
        processor.writeStringValue(9, "Lead Time");
        processor.writeStringValue(10, "Forecast Site Date");
        processor.writeStringValue(11, "Actual Site Date");
        processor.writeStringValue(12, "Required on Site Date");
        processor.writeStringValue(13, "Var");
    }

    public Integer datePart(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            long diferenciaEn_ms = date1.getTime() - date2.getTime();
            long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
            return (int) dias;
        } else {
            return null;
        }
    }

}

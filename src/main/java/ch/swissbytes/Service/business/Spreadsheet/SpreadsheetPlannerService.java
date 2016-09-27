package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LanguagePreference;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.SpreadsheetProcessor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by Christian on 21/07/2015.
 */
public class SpreadsheetPlannerService implements Serializable {

    public static final Logger log = Logger.getLogger(SpreadsheetPlannerService.class.getName());

    @Inject
    private ScopeSupplyDao supplyDao;
    @Inject
    private CashflowService cashflowService;
    @Inject
    private ScopeSupplyService scopeSupplyService;
    @Inject
    public SpreadsheetProcessor processor;
    @Inject
    public LanguagePreference languagePreference;
    @Inject
    private Configuration configuration;

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    //public Configuration configuration = new Configuration();

    int rowNo;

    private String formatDateReport = "dd MMM yyyy";

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
        String fileName = dateStr.toUpperCase() + " - " + "Commitments.xlsx";
        return fileName;
    }

    public void processWorkbook(final List<PurchaseOrderEntity> list) {
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        processor.createSpreadsheetWithoutPassword("PkgHdr");
        prepareWithColumns();
        createHeaderJS();
        createHeaderPO();
        generateSpreadsheetPurchaseOrderDetail(list);
    }

    private void prepareWithColumns() {
        processor.configureWithColumn(0,5000);
        processor.configureWithColumn(1,5000);
        processor.configureWithColumn(3,3000);
        processor.configureWithColumn(2,3000);
        processor.configureWithColumn(4,3000);
        processor.configureWithColumn(5,3000);
        processor.configureWithColumn(6,3000);
        processor.configureWithColumn(7,3000);
        processor.configureWithColumn(8,3000);
        processor.configureWithColumn(9,3000);
        processor.configureWithColumn(10,5000);//item description
        processor.configureWithColumn(11,3000);
        processor.configureWithColumn(12,3000);
        processor.configureWithColumn(13,3000);
        processor.configureWithColumn(14,3000);
        processor.configureWithColumn(15,3000);
        processor.configureWithColumn(16,3000);
        processor.configureWithColumn(17,3000);
        processor.configureWithColumn(18,3000);
        processor.configureWithColumn(19,3000);
        processor.configureWithColumn(20,3000);
    }
    private void createHeaderPO() {
        processor.createRowWithHeight(2,(short)600);
        List<XSSFCellStyle> styles = new ArrayList<>();
        styles.add(processor.getStyleAlignTop());
        styles.add(processor.getStyleVerticalJustify());
        processor.writeStringValueWithStyles(0, "PO", styles);
        processor.writeStringValueWithStyles(1, "Title",styles);
        processor.writeStringValueWithStyles(2, "PO Del Date",styles);
        processor.writeStringValueWithStyles(3, "INCO Term",styles);
        processor.writeStringValueWithStyles(4, "Supplier",styles);
        processor.writeStringValueWithStyles(5, "Status",styles);
        processor.writeStringValueWithStyles(6, "Rfe",styles);
        processor.writeStringValueWithStyles(7, "Item No",styles);
        processor.writeStringValueWithStyles(8, "Qty",styles);
        processor.writeStringValueWithStyles(9, "Unit",styles);
        processor.writeStringValueWithStyles(10, "Item Description",styles);
        processor.writeStringValueWithStyles(11, "Equipment Tag",styles);
        processor.writeStringValueWithStyles(12, "Full Inco Term",styles);
        processor.writeStringValueWithStyles(13, "PO Delivery Date",styles);
        processor.writeStringValueWithStyles(14, "Forecast Ex Works Date",styles);
        processor.writeStringValueWithStyles(15, "Actual Ex Works",styles);
        processor.writeStringValueWithStyles(16, "Lead Time",styles);
        processor.writeStringValueWithStyles(17, "Forecast Site Date",styles);
        processor.writeStringValueWithStyles(18, "Actual Site Date",styles);
        processor.writeStringValueWithStyles(19, "Required on Site Date",styles);
        processor.writeStringValueWithStyles(20, "Var",styles);
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list) {
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        for (PurchaseOrderEntity entity : list) {
            List<ScopeSupplyEntity> scopeSupplyListList = scopeSupplyService.scopeSupplyListByPOId(entity.getId());
            if(hasScopeSupplyExcludeFromExpediting(scopeSupplyListList)){
                if (!scopeSupplyListList.isEmpty()) {
                    for (ScopeSupplyEntity ss : scopeSupplyListList) {
                        if(ss.getExcludeFromExpediting()==null || !ss.getExcludeFromExpediting()) {
                            processor.createRow(rowNo);
                            processor.writeStringValue(0, entity.getProjectEntity().getProjectNumber() + " " + entity.getPo() + " v" + entity.getVariation());
                            processor.writeStringValue(1, entity.getPoTitle());
                            processor.writeStringValue(2, entity.getPoDeliveryDate()!=null?Util.toLocal(entity.getPoDeliveryDate(), configuration.getTimeZone(), configuration.getFormatDate()):"");
                            processor.writeStringValue(3, StringUtils.isNotEmpty(entity.getFullIncoTerms()) ? entity.getFullIncoTerms() : "");
                            processor.writeStringValue(4, entity.getPurchaseOrderProcurementEntity().getSupplier().getCompany());
                            processor.writeStringValue(5, Util.getNamesStatuses(entity.getExpeditingStatus()).toUpperCase());
                            processor.writeStringValue(6, entity.getResponsibleExpediting() != null ? entity.getResponsibleExpediting() : "");
                            processor.writeStringValue(7, StringUtils.isNotEmpty(ss.getCode()) ? ss.getCode() : "");
                            processor.writeStringValue(8, ss.getQuantity() != null ? decFormat.format(ss.getQuantity()) : "");
                            processor.writeStringValue(9, StringUtils.isNotEmpty(ss.getUnit()) ? ss.getUnit() : "");
                            processor.writeStringValue(10, StringUtils.isNotEmpty(ss.getDescription()) ? ss.getDescription() : "");
                            processor.writeStringValue(11, StringUtils.isNotEmpty(ss.getTagNo()) ? ss.getTagNo() : "");
                            processor.writeStringValue(12, StringUtils.isNotEmpty(ss.getSpIncoTermDescription()) ? ss.getSpIncoTermDescription() : "");
                            processor.writeStringValue(13, ss.getPoDeliveryDate() != null ? Util.toLocal(ss.getPoDeliveryDate(), configuration.getTimeZone(), formatDateReport) : "");
                            processor.writeStringValue(14, ss.getForecastExWorkDate() != null ? Util.toLocal(ss.getForecastExWorkDate(), configuration.getTimeZone(), formatDateReport) : "");
                            processor.writeStringValue(15, ss.getActualExWorkDate() != null ? Util.toLocal(ss.getActualExWorkDate(), configuration.getTimeZone(), formatDateReport) : "");
                            String deliveryQt = ss.getDeliveryLeadTimeQt() != null ? String.valueOf(ss.getDeliveryLeadTimeQt().intValue()) : "";
                            String deliveryMt = ss.getDeliveryLeadTimeMs() != null ? bundle.getString("measurement.time." + ss.getDeliveryLeadTimeMs().name().toLowerCase()) : "";
                            processor.writeStringValue(16, deliveryQt + " " + deliveryMt);
                            processor.writeStringValue(17, ss.getForecastSiteDate() != null ? Util.toLocal(ss.getForecastSiteDate(), configuration.getTimeZone(), formatDateReport) : "");
                            processor.writeStringValue(18, ss.getActualSiteDate() != null ? Util.toLocal(ss.getActualSiteDate(), configuration.getTimeZone(), formatDateReport) : "");
                            processor.writeStringValue(19, ss.getRequiredSiteDate() != null ? Util.toLocal(ss.getRequiredSiteDate(), configuration.getTimeZone(), formatDateReport) : "");
                            Integer dateDiff = datePart(ss.getRequiredSiteDate(), ss.getForecastSiteDate());
                            processor.writeStringValue(20, dateDiff != null ? String.valueOf(dateDiff.intValue()) : "");
                            rowNo++;
                        }
                    }
                }
            }
        }
    }

    private boolean hasScopeSupplyExcludeFromExpediting(List<ScopeSupplyEntity> list) {
        for(ScopeSupplyEntity ss : list){
            if(ss.getExcludeFromExpediting()==null || !ss.getExcludeFromExpediting().booleanValue()){
                return true;
            }
        }
        return false;
    }

    private void createHeaderJS() {
        processor.createRow(0);
        processor.writeStringBoldValue(0, "Planner Report");
        processor.writeStringBoldValue(1, Util.toLocal(new Date(), configuration.getTimeZone(), "MMM, dd yyyy"));
        processor.createRow(1);
    }

    public Integer datePart(Date date1, Date date2) {
        if(date1!=null&&date2!=null) {
            long diferenciaEn_ms = date1.getTime() - date2.getTime();
            long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
            return (int) dias;
        }else{
            return null;
        }
    }

}

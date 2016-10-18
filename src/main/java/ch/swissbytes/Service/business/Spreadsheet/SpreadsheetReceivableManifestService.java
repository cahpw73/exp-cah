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

import javax.inject.Inject;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by Christian on 21/07/2015.
 */
public class SpreadsheetReceivableManifestService implements Serializable {

    public static final Logger log = Logger.getLogger(SpreadsheetReceivableManifestService.class.getName());

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
        processor.configureWithColumn(0,8000);
        processor.configureWithColumn(1,4000);
        processor.configureWithColumn(3,3000);
        processor.configureWithColumn(2,3000);
        processor.configureWithColumn(4,3000);
        processor.configureWithColumn(5,12000);
        processor.configureWithColumn(6,5000);
        processor.configureWithColumn(7,12000);
        processor.configureWithColumn(8,5000);
        processor.configureWithColumn(9,6000);
        processor.configureWithColumn(10,5000);
    }
    private void createHeaderPO() {
        processor.createRow(2);
        processor.writeStringValue(0, "Project");
        processor.writeStringValue(1, "PO Number");
        processor.writeStringValue(2, "Var.");
        processor.writeStringValue(3, "Item No");
        processor.writeStringValue(4, "Qty Unit");
        processor.writeStringValue(5, "Item Description");
        processor.writeStringValue(6, "Tag No");
        processor.writeStringValue(7, "Shipping Details");
        processor.writeStringValue(8, "Actual Site Date");
        processor.writeStringValue(9, "Required on Site Date");
        processor.writeStringValue(10, "Location");
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
                            processor.writeStringValue(0, entity.getProjectEntity().getProjectNumber());
                            processor.writeStringValue(1, entity.getPo());
                            processor.writeStringValue(2, entity.getVariation());
                            processor.writeStringValue(3, ss.getCode());
                            processor.writeStringValue(4, ss.getQuantity() != null ? decFormat.format(ss.getQuantity()) : "");
                            processor.writeStringValue(5, ss.getDescription());
                            processor.writeStringValue(6, ss.getTagNo());
                            processor.writeStringValue(7, ss.getShippingDetails());
                            processor.writeStringValue(8, ss.getActualSiteDate()!=null?Util.toLocal(ss.getActualSiteDate(), configuration.getTimeZone(), configuration.getFormatDate()):"");
                            processor.writeStringValue(9, ss.getRequiredSiteDate()!=null?Util.toLocal(ss.getRequiredSiteDate(), configuration.getTimeZone(), configuration.getFormatDate()):"");
                            processor.writeStringValue(10, "");
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
        processor.writeStringBoldValue(0, "Receivable Manifest Report");
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

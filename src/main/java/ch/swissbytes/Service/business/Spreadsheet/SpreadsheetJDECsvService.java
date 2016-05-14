package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.RetentionFormEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.SpreadsheetProcessor;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 21/07/2015.
 */
public class SpreadsheetJDECsvService implements Serializable {

    public static final Logger log = Logger.getLogger(SpreadsheetJDECsvService.class.getName());

    @Inject
    private PurchaseOrderService service;
    @Inject
    private ScopeSupplyDao supplyDao;
    @Inject
    private CashflowService cashflowService;
    @Inject
    private ScopeSupplyService scopeSupplyService;
    @Inject
    private Util util;

    public SpreadsheetProcessor processor;

    public Configuration configuration = new Configuration();

    int rowNo;
    int rowNoMilestone;

    private static final String packageHeaderInformation="PkgHdr.xlsx";
    private static final String packageScheduleInformation="PkgScInf.xlsx";

    public void generateWorkbookToExport(final List<PurchaseOrderEntity> list, String folderName) throws Exception {
        rowNo = 0;
        String pathJDE = System.getProperty("fqmes.path.export.jde");
        pathJDE = pathJDE.replace("{project_field}", folderName);
        log.info("Create spreadSheet for JDE PkgHdr csv");
        processWorkbook(list);
        processor.doSaveWorkBook(pathJDE, packageHeaderInformation);

        log.info("written JDE PkgHdr CSV successfully...");
        log.info("Converting PkgHdr.xlsx file to csv file");

        convertToCsv(pathJDE+ File.separator+packageHeaderInformation,pathJDE);

        log.info("Deleting PkgHdr.xlsx file");
        deleteFileTemporal(pathJDE+ File.separator+packageHeaderInformation);
        log.info("process to Export JDE PkgHdr.xlsx CSV file completed");

        log.info("Create spreadSheet for JDE PkgScInf csv");
        rowNoMilestone = 0;
        processWorkbookForMilestone(list);
        processor.doSaveWorkBook(pathJDE, packageScheduleInformation);
        log.info("written JDE PkgScInf CSV successfully...");
        convertToCsv(pathJDE+ File.separator+packageScheduleInformation,pathJDE);
        log.info("process to Export JDE PkgScInf.xlsx CSV file completed");
        deleteFileTemporal(pathJDE+ File.separator+packageScheduleInformation);
        log.info("process to Export JDE PkgScInf.xlsx CSV file completed");
    }

    public void processWorkbook(final List<PurchaseOrderEntity> list) {
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        createPagePackageHeader(list);
        //createPagePackageMilestone(list);
    }

    public void processWorkbookForMilestone(final List<PurchaseOrderEntity> list) {
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        createPagePackageMilestone(list);
    }

    private void createPagePackageHeader(final List<PurchaseOrderEntity> list) {
        processor.createSpreadsheet("PkgHdr");
        generateSpreadsheetPurchaseOrderDetail(list);
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list) {
        for (PurchaseOrderEntity entity : list) {
            List<ItemEntity> itemEntityList = scopeSupplyService.getItemsOrderedByCurrency(entity.getId());
            if (!itemEntityList.isEmpty()) {
                PurchaseOrderEntity originalPO = null;
                if (entity.getOrderedVariation() > 1) {
                    originalPO = service.findFirstPO(entity);
                }
                CashflowEntity cashflowEntity = getCashflowEntity(entity);
                boolean hasOneItem = itemEntityList.size() == 1;
                processor.createRow(rowNo);
                ItemEntity item = itemEntityList.get(0);
                prepareFirstLineContent(entity, item, hasOneItem, originalPO, cashflowEntity);
                itemEntityList.remove(0);
                for (ItemEntity ss : itemEntityList) {
                    rowNo++;
                    processor.createRow(rowNo);
                    prepareDetailContent(ss);
                }
            }
        }
    }

    private void prepareFirstLineContent(PurchaseOrderEntity entity, ItemEntity item, boolean hasOneItem, PurchaseOrderEntity originalPO, CashflowEntity cashflowEntity) {
        Util util = new Util();
        util.setConfiguration(configuration);
        processor.writeStringValue(0, entity.getProjectEntity().getTitle() != null ? entity.getProjectEntity().getTitle() : "");
        processor.writeStringValue(1, entity.getPo() != null ? entity.getPo() : "");
        processor.writeStringValue(2, entity.getPoTitle() != null ? entity.getPoTitle() : "");
        processor.writeStringValue(3, entity.getPurchaseOrderProcurementEntity().getPoint() != null ? entity.getPurchaseOrderProcurementEntity().getPoint() : "");
        processor.writeStringValue(4, entity.getPurchaseOrderProcurementEntity().getSupplier() != null ? entity.getPurchaseOrderProcurementEntity().getSupplier().getSupplierId() : "");
        processor.writeStringValue(5, entity.getPurchaseOrderProcurementEntity().getDeliveryInstruction() != null ? entity.getPurchaseOrderProcurementEntity().getDeliveryInstruction() : "");
        processor.writeStringValue(6, collectMRNo(entity));
        processor.writeStringValue(7, collectRTFNo(entity));
        Date originalOrderDate = originalPO != null ? originalPO.getPurchaseOrderProcurementEntity().getOrderDate() : entity.getPurchaseOrderProcurementEntity().getOrderDate();
        processor.writeStringValue(8, originalOrderDate != null ? configuration.convertDateToExportFileCsv(originalOrderDate) : "");
        Date originalDeliveryDate = originalPO != null ? originalPO.getPoDeliveryDate() : entity.getPoDeliveryDate();
        processor.writeStringValue(9, originalDeliveryDate != null ? configuration.convertDateToExportFileCsv(originalDeliveryDate) : "");
        processor.writeStringValue(10, entity.getPurchaseOrderProcurementEntity().getLiquidatedDamagesApplicable() != null ? BooleanUtils.toStringYesNo(entity.getPurchaseOrderProcurementEntity().getLiquidatedDamagesApplicable()).toUpperCase() : "");
        processor.writeStringValue(11, entity.getPurchaseOrderProcurementEntity().getExchangeRateVariation() != null ? BooleanUtils.toStringYesNo(entity.getPurchaseOrderProcurementEntity().getExchangeRateVariation()).toUpperCase() : "");
        processor.writeStringValue(12, entity.getPurchaseOrderProcurementEntity().getVendorDrawingData() != null ? BooleanUtils.toStringYesNo(entity.getPurchaseOrderProcurementEntity().getVendorDrawingData()).toUpperCase() : "");
        processor.writeStringValue(13, getValueBankGuarantee(cashflowEntity));
        processor.writeStringValue(14, getValueCashflowPercentage(cashflowEntity));

        processor.writeStringValue(15, item.getCode() != null ? item.getCode() : "");
        processor.writeStringValue(16, item.getQuantity() != null ? item.getQuantity().toString() : "");
        processor.writeStringValue(17, item.getUnit() != null ? item.getUnit() : "");
        processor.writeStringValue(18, item.getDescription() != null ? item.getDescription() : "");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(19, item.getCost() != null ? decFormat.format(item.getCost()) : "");
        processor.writeStringValue(20, item.getProjectCurrency().getCurrency().getCode());
        processor.writeStringValue(21, item.getCostCode()!=null?item.getCostCode():"");

    }

    private void prepareDetailContent(ItemEntity item) {
        processor.writeStringValue(0, "");
        processor.writeStringValue(1, "");
        processor.writeStringValue(2, "");
        processor.writeStringValue(3, "");
        processor.writeStringValue(4, "");
        processor.writeStringValue(5, "");
        processor.writeStringValue(6, "");
        processor.writeStringValue(7, "");
        processor.writeStringValue(8, "");
        processor.writeStringValue(9, "");
        processor.writeStringValue(10, "");
        processor.writeStringValue(11, "");
        processor.writeStringValue(12, "");
        processor.writeStringValue(13, "");
        processor.writeStringValue(14, "");

        processor.writeStringValue(15, item.getCode() != null ? item.getCode() : "");
        processor.writeStringValue(16, item.getQuantity() != null ? item.getQuantity().toString() : "");
        processor.writeStringValue(17, item.getUnit() != null ? item.getUnit() : "");
        processor.writeStringValue(18, item.getDescription() != null ? item.getDescription() : "");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(19, item.getCost() != null ? decFormat.format(item.getCost()) : "");
        processor.writeStringValue(20, item.getProjectCurrency().getCurrency().getCode());
        processor.writeStringValue(21, item.getCostCode()!=null?item.getCostCode():"");
    }

    private String collectMRNo(PurchaseOrderEntity po) {
        StringBuilder sb = new StringBuilder();
        for (RequisitionEntity requisitionEntity : po.getPurchaseOrderProcurementEntity().getRequisitions()) {
            sb.append(requisitionEntity.getRequisitionNumber());
            sb.append("-");
        }
        return sb.toString().length() > 0 ? sb.toString().substring(0, sb.toString().length() - 1) : "";
    }

    private String collectRTFNo(PurchaseOrderEntity po) {
        StringBuilder sb = new StringBuilder();
        for (RequisitionEntity requisitionEntity : po.getPurchaseOrderProcurementEntity().getRequisitions()) {
            if (StringUtils.isNotEmpty(requisitionEntity.getrTFNo()) && StringUtils.isNotBlank(requisitionEntity.getrTFNo())) {
                sb.append(requisitionEntity.getrTFNo());
            } else if (StringUtils.isNotEmpty(requisitionEntity.getOriginator()) && StringUtils.isNotBlank(requisitionEntity.getOriginator())) {
                sb.append(requisitionEntity.getOriginator());
            }
            sb.append("-");
        }
        return sb.toString().length() > 0 ? sb.toString().substring(0, sb.toString().length() - 1) : "";
    }

    private String getValueBankGuarantee(CashflowEntity cashflowEntity) {
        if (hasRetentionApplicable(cashflowEntity)) {
            return valueFromRetentionForm(cashflowEntity);
        } else if (hasSecurityDeposit(cashflowEntity)) {
            return valueFromRetentionForm(cashflowEntity);
        }
        return "";
    }

    private String valueFromRetentionForm(CashflowEntity entity) {
        boolean isBankGuarantee = StringUtils.equals(RetentionFormEnum.BANK_GUARANTEE.getLabel(), entity.getForm());
        boolean isInsuranceBond = StringUtils.equals(RetentionFormEnum.INSURANCE_BOND.getLabel(), entity.getForm());
        boolean isOutBalance = StringUtils.equals("Out of Balance", entity.getForm());
        return isBankGuarantee ? "B" : (isInsuranceBond ? "I" : (isOutBalance ? "O" : ""));
    }

    private CashflowEntity getCashflowEntity(PurchaseOrderEntity entity) {
        List<CashflowEntity> cashflows = cashflowService.findByPoId(entity.getPurchaseOrderProcurementEntity().getId());
        CashflowEntity cashflowEntity = cashflows.size() > 0 ? cashflows.get(0) : null;
        return cashflowEntity;
    }

    private boolean hasRetentionApplicable(CashflowEntity entity) {
        return entity != null && entity.getApplyRetention() ? entity.getApplyRetention() : false;
    }

    private boolean hasSecurityDeposit(CashflowEntity entity) {
        return entity != null && entity.getApplyRetentionSecurityDeposit() ? entity.getApplyRetentionSecurityDeposit() : false;
    }

    private String getValueCashflowPercentage(CashflowEntity cashflowEntity) {
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        if (hasRetentionApplicable(cashflowEntity)) {
            return decFormat.format(cashflowEntity.getPercentage());
        } else if (hasSecurityDeposit(cashflowEntity)) {
            return decFormat.format(cashflowEntity.getPercentageSecurityDeposit());
        }
        return "";
    }

    public void convertToCsv(String sourceFile, String destinationFolder){
        try {
            ToCSV converter = new ToCSV();
            converter.convertExcelToCSV(sourceFile, destinationFolder);
        }
        // It is not wise to have such a wide catch clause - Exception is very
        // close to being at the top of the inheritance hierarchy - though it
        // will suffice for this example as it is really not possible to recover
        // easilly from an exceptional set of circumstances at this point in the
        // program. It should however, ideally be replaced with one or more
        // catch clauses optimised to handle more specific problems.
        catch(Exception ex) {
            log.info("Caught an: " + ex.getClass().getName());
            log.info("Message: " + ex.getMessage());
            log.info("Stacktrace follows:.....");
            ex.printStackTrace(System.out);
        }
    }

    public void deleteFileTemporal(String filePath){
        try{
            File file = new File(filePath);
            if(file.delete()){
                log.info(file.getName() + " is deleted!");
            }else{
                log.info("Delete operation is failed.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    ///-----------------------------------------------

    public InputStream generateWorkbook(final List<PurchaseOrderEntity> list) {
        processWorkbook(list);
        log.info("written successfully...");
        return processor.getContentSheet();
    }

    private void prepareWithColumns() {
        processor.configureWithColumn(2, 3000);
        processor.configureWithColumn(3, 8000);
        processor.configureWithColumn(5, 10000);
        processor.configureWithColumn(7, 4000);
        processor.configureWithColumn(9, 4000);
        processor.configureWithColumn(10, 4000);
        processor.configureWithColumn(11, 8500);
        processor.configureWithColumn(12, 3000);
        processor.configureWithColumn(13, 2500);
        processor.configureWithColumn(14, 2500);
    }

    private void createPagePackageMilestone(final List<PurchaseOrderEntity> list) {
        processor.createSpreadsheet("PkgScInf");
        /*createHeaderCMS(list.get(0));
        createHeaderMilestone();*/
        generateSpreadsheetCashflowDetail(list);
    }

    private void createRowTotalPrice(PurchaseOrderEntity entity, BigDecimal totalForCurrency) {
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.createRow(rowNo);
        processor.writeStringBoldValue(9, entity.getPo().toUpperCase() + "v" + entity.getVariation() + " TOTAL");
        processor.writeStringBoldValue(10, totalForCurrency != null ? decFormat.format(totalForCurrency) : "");
        processor.writeStringValue(13, entity.getPurchaseOrderProcurementEntity().getCashflow().getApplyRetention() ? "Yes" : "No");
        processor.writeStringValue(14, entity.getPurchaseOrderProcurementEntity().getCashflow().getApplyRetentionSecurityDeposit() ? "Yes" : "No");
    }

    private void createHeaderPO() {
        processor.createRow(1);
        processor.writeStringBoldValue(0, "PO No.");
        processor.writeStringBoldValue(1, "Var");
        processor.writeStringBoldValue(2, "PO Date");
        processor.writeStringBoldValue(3, "Supplier");
        processor.writeStringBoldValue(4, "Item");
        processor.writeStringBoldValue(5, "Description");
        processor.writeStringBoldValue(6, "Currency");
        processor.writeStringBoldValue(7, "Unit Price");
        processor.writeStringBoldValue(8, "UOM");
        processor.writeStringBoldValue(9, "Qty");
        processor.writeStringBoldValue(10, "Total Price");
        processor.writeStringBoldValue(11, "Terms");
        processor.writeStringBoldValue(12, "Cost Code");
        processor.writeStringBoldValue(13, "Retention");
        processor.writeStringBoldValue(14, "Security");
    }

    private void createHeaderCMS(PurchaseOrderEntity entity) {
        processor.createRow(0);
        processor.writeStringBoldValue(0, "PROJECT: ");
        processor.writeStringValue(1, entity.getProjectEntity().getProjectNumber());
        processor.writeStringBoldValue(5, "EXPORT TO JDE");
        processor.writeStringBoldValue(6, "DATE:");
        processor.writeStringValue(7, configuration.convertDateToExportFile(new Date()));
    }

    private void generateSpreadsheetCashflowDetail(List<PurchaseOrderEntity> list) {
        for (PurchaseOrderEntity entity : list) {
            List<CashflowDetailEntity> cashflowDetailList = cashflowService.findOrderedByCurrencyAndItem(entity.getPurchaseOrderProcurementEntity().getCashflow().getId());
            if (!cashflowDetailList.isEmpty()) {
                processor.createRow(rowNoMilestone);
                CashflowDetailEntity cashflowDetail = cashflowDetailList.get(0);
                boolean hasOneMilestone = cashflowDetailList.size() == 1;
                int cashflowDetailSize = cashflowDetailList.size();
                prepareFirstLineContentCashflowDetail(entity, cashflowDetail, hasOneMilestone);
                cashflowDetailList.remove(0);
                rowNoMilestone++;
                for (CashflowDetailEntity cf : cashflowDetailList) {
                    processor.createRow(rowNoMilestone);
                    prepareDetailContentCashflowDetail(entity, cf);
                    rowNoMilestone++;
                }
            }
        }
    }

    private void prepareFirstLineContentCashflowDetail(PurchaseOrderEntity entity, CashflowDetailEntity cashflowDetail, boolean hasOneMilestone) {
        Util util = new Util();
        util.setConfiguration(configuration);
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(0, entity.getProjectEntity().getTitle() != null ? entity.getProjectEntity().getTitle() : "");
        processor.writeStringValue(1, entity.getPo() != null ? entity.getPo() : "");
        processor.writeStringValue(2, cashflowDetail.getItem() != null ? cashflowDetail.getItem() : "");
        processor.writeStringValue(3, cashflowDetail.getMilestone() != null ? cashflowDetail.getMilestone() : "");
        processor.writeStringValue(4, cashflowDetail.getProjectCurrency() != null ? cashflowDetail.getProjectCurrency().getCurrency().getCode() : "");
        processor.writeStringValue(5, cashflowDetail.getOrderAmt() != null ? decFormat.format(cashflowDetail.getOrderAmt()) : "");
        processor.writeStringValue(6, cashflowDetail.getPaymentDate() != null ? configuration.convertDateToExportFile(cashflowDetail.getPaymentDate()) : "");
    }

    private void prepareDetailContentCashflowDetail(PurchaseOrderEntity entity, CashflowDetailEntity cashflowDetail) {
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(2, cashflowDetail.getItem() != null ? cashflowDetail.getItem() : "");
        processor.writeStringValue(3, cashflowDetail.getMilestone() != null ? cashflowDetail.getMilestone() : "");
        processor.writeStringValue(4, cashflowDetail.getProjectCurrency() != null ? cashflowDetail.getProjectCurrency().getCurrency().getCode() : "");
        processor.writeStringValue(5, cashflowDetail.getOrderAmt() != null ? decFormat.format(cashflowDetail.getOrderAmt()) : "");
        processor.writeStringValue(6, cashflowDetail.getPaymentDate() != null ? configuration.convertDateToExportFile(cashflowDetail.getPaymentDate()) : "");
    }

    private void createHeaderMilestone() {
        processor.createRow(1);
        processor.writeStringBoldValue(0, "Po");
        processor.writeStringBoldValue(1, "Variation");
        processor.writeStringBoldValue(2, "Milestone Number");
        processor.writeStringBoldValue(3, "Milestone Description");
        processor.writeStringBoldValue(4, "Currency");
        processor.writeStringBoldValue(5, "Amount");
        processor.writeStringBoldValue(6, "Date");
    }

}

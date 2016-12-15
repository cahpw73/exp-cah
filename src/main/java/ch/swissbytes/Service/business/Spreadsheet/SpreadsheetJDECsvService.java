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
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private static final String packageHeaderInformation = "PkgHdr.xlsx";
    private static final String packageScheduleInformation = "PkgScInf.xlsx";

    public List<String> generateJDECsvFileAndGetPaths(final List<PurchaseOrderEntity> list, String folderName) throws Exception {
        List<String> paths = new ArrayList<>();

        String pathPkgHdr = generateWorkbookToPkgHdr(list, folderName);
        paths.add(pathPkgHdr);

        String pathPkgInf = generateWorkbookToPkgInf(list, folderName);
        paths.add(pathPkgInf);
        return paths;
    }

    public void deleteTemporalFiles(List<String> paths){
        for (String p : paths){
            deleteFileTemporal(p);
        }
    }

    public void generateWorkbookToExport(final List<PurchaseOrderEntity> list, String folderName) throws IOException {
        String pathPkgHdr = generateWorkbookToPkgHdr(list,folderName);
        deleteFileTemporal(pathPkgHdr);

        String pathPkgInf = generateWorkbookToPkgInf(list,folderName);
        deleteFileTemporal(pathPkgInf);
    }

    private String generateWorkbookToPkgHdr(final List<PurchaseOrderEntity> list, String folderName) throws IOException{
        rowNo = 0;
        String pathJDE = System.getProperty("fqmes.path.export.jde.csv.package.header");
        //pathJDE = pathJDE.replace("{project_field}", folderName);
        log.info("Create spreadSheet for JDE PkgHdr csv");
        processWorkbook(list);
        String fileNamePckIGenerated = generateFileName(packageHeaderInformation,folderName);
        processor.doSaveWorkBook(pathJDE, fileNamePckIGenerated);

        log.info("written JDE PkgHdr CSV successfully...");
        log.info("Converting PkgHdr.xlsx file to csv file");

        convertToCsv(pathJDE + File.separator + fileNamePckIGenerated, pathJDE);

        log.info("Deleting PkgHdr.xlsx file");
        log.info("process to Export JDE PkgHdr.xlsx CSV file completed");
        return pathJDE + File.separator + fileNamePckIGenerated;
    }

    private String generateWorkbookToPkgInf(final List<PurchaseOrderEntity> list, String folderName) throws IOException{
        String pathJDE = System.getProperty("fqmes.path.export.jde.csv.package.schedule.info");
        //pathJDE = pathJDE.replace("{project_field}", folderName);
        log.info("Create spreadSheet for JDE PkgScInf csv");
        rowNoMilestone = 0;
        processWorkbookForMilestone(list);
        String fileNameScheludeIGenerated = generateFileName(packageScheduleInformation,folderName);
        processor.doSaveWorkBook(pathJDE, fileNameScheludeIGenerated);
        log.info("written JDE PkgScInf CSV successfully...");
        convertToCsv(pathJDE + File.separator + fileNameScheludeIGenerated, pathJDE);
        log.info("process to Export JDE PkgScInf.xlsx CSV file completed");
        log.info("process to Export JDE PkgScInf.xlsx CSV file completed");
        return pathJDE + File.separator + fileNameScheludeIGenerated;
    }

    public void processWorkbook(final List<PurchaseOrderEntity> list) {
        log.info("Begin processWorkbook");
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        createPagePackageHeader(list);
        log.info("End processWorkbook");
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
        log.info("generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list)");
        for (PurchaseOrderEntity entity : list) {
            log.info("PO id = " + entity.getId());
            List<ItemEntity> itemEntityList = scopeSupplyService.getItemsOrderedByCurrency(entity.getId());
            if (!itemEntityList.isEmpty()) {
                PurchaseOrderEntity originalPO = null;
                if (entity.getOrderedVariation() > 1) {
                    originalPO = service.findFirstPO(entity);
                }
                CashflowEntity cashflowEntity = getCashflowEntity(entity);
                processor.createRow(rowNo);
                for (ItemEntity ss : itemEntityList) {
                    log.info("ItemEntity Id = " + ss.getId());
                    processor.createRow(rowNo);
                    fillingDetailContent(entity, ss, originalPO, cashflowEntity);
                    rowNo++;
                    log.info("rowNo: " + rowNo);
                }
            }
        }
    }

    private void fillingDetailContent(PurchaseOrderEntity entity, ItemEntity item, PurchaseOrderEntity originalPO, CashflowEntity cashflowEntity) {
        Util util = new Util();
        util.setConfiguration(configuration);
        processor.writeStringValue(0, entity.getProjectEntity().getTitle() != null ? entity.getProjectEntity().getTitle() : " ");
        processor.writeStringValue(1, entity.getPo() != null ? entity.getPo() : " ");
        processor.writeStringValue(2, entity.getPoTitle() != null ? entity.getPoTitle() : " ");
        processor.writeStringValue(3, entity.getPurchaseOrderProcurementEntity().getPoint() != null ? entity.getPurchaseOrderProcurementEntity().getPoint() : " ");
        processor.writeStringValue(4, entity.getPurchaseOrderProcurementEntity().getSupplier() != null ? entity.getPurchaseOrderProcurementEntity().getSupplier().getSupplierId() : " ");
        entity.getPurchaseOrderProcurementEntity().setDeliveryInstruction(entity.getPurchaseOrderProcurementEntity().getDeliveryInstruction().replace("\n", "").replace("\r", ""));
        processor.writeStringValue(5, entity.getPurchaseOrderProcurementEntity().getDeliveryInstruction() != null ? (entity.getPurchaseOrderProcurementEntity().getDeliveryInstruction().length() >= 30 ? entity.getPurchaseOrderProcurementEntity().getDeliveryInstruction().substring(0, 29) : entity.getPurchaseOrderProcurementEntity().getDeliveryInstruction()) : " ");
        processor.writeStringValue(6, collectMRNo(entity));
        processor.writeStringValue(7, collectRTFNo(entity));
        Date originalOrderDate = originalPO != null ? originalPO.getPurchaseOrderProcurementEntity().getOrderDate() : entity.getPurchaseOrderProcurementEntity().getOrderDate();
        processor.writeStringValue(8, originalOrderDate != null ? configuration.convertDateToExportFileCsv(originalOrderDate) : " ");
        Date originalDeliveryDate = originalPO != null ? originalPO.getPoDeliveryDate() : entity.getPoDeliveryDate();
        processor.writeStringValue(9, originalDeliveryDate != null ? configuration.convertDateToExportFileCsv(originalDeliveryDate) : " ");
        processor.writeStringValue(10, entity.getPurchaseOrderProcurementEntity().getLiquidatedDamagesApplicable() != null ? BooleanUtils.toStringYesNo(entity.getPurchaseOrderProcurementEntity().getLiquidatedDamagesApplicable()).toUpperCase() : " ");
        processor.writeStringValue(11, entity.getPurchaseOrderProcurementEntity().getExchangeRateVariation() != null ? BooleanUtils.toStringYesNo(entity.getPurchaseOrderProcurementEntity().getExchangeRateVariation()).toUpperCase() : " ");
        processor.writeStringValue(12, entity.getPurchaseOrderProcurementEntity().getVendorDrawingData() != null ? BooleanUtils.toStringYesNo(entity.getPurchaseOrderProcurementEntity().getVendorDrawingData()).toUpperCase() : " ");
        processor.writeStringValue(13, getValueBankGuarantee(cashflowEntity));
        processor.writeStringValue(14, getValueCashflowPercentage(cashflowEntity));

        processor.writeStringValue(15, item.getCode() != null ? item.getCode() : " ");
        processor.writeStringValue(16, item.getQuantity() != null ? item.getQuantity().toString() : " ");
        processor.writeStringValue(17, item.getUnit() != null ? item.getUnit() : " ");
        log.info("item.getDescription() Length A = " + (item.getDescription() != null ? item.getDescription().length() : "0"));
        item.setDescription(item.getDescription().replace("\n", "").replace("\r", ""));
        log.info("item.getDescription() Length B = " + (item.getDescription() != null ? item.getDescription().length() : "0"));
        processor.writeStringValue(18, item.getDescription() != null ? (item.getDescription().length() >= 30 ? item.getDescription().substring(0, 29) : item.getDescription()) : " ");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimalWithoutComma());
        processor.writeStringValue(19, item.getCost() != null ? decFormat.format(item.getCost()) : " ");
        String currencyCode = (item.getProjectCurrency() != null && item.getProjectCurrency().getCurrency() != null && item.getProjectCurrency().getCurrency().getCode() != null) ? item.getProjectCurrency().getCurrency().getCode() : " ";
        processor.writeStringValue(20, currencyCode);
        processor.writeStringValue(21, item.getCostCode() != null ? item.getCostCode() : " ");

    }

    private String collectMRNo(PurchaseOrderEntity po) {
        StringBuilder sb = new StringBuilder();
        for (RequisitionEntity requisitionEntity : po.getPurchaseOrderProcurementEntity().getRequisitions()) {
            sb.append(requisitionEntity.getRequisitionNumber());
            sb.append("-");
        }
        return sb.toString().length() > 0 ? sb.toString().substring(0, sb.toString().length() - 1) : " ";
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
        return sb.toString().length() > 0 ? sb.toString().substring(0, sb.toString().length() - 1) : " ";
    }

    private String getValueBankGuarantee(CashflowEntity cashflowEntity) {
        if (hasRetentionApplicable(cashflowEntity)) {
            return valueFromRetentionForm(cashflowEntity);
        } else if (hasSecurityDeposit(cashflowEntity)) {
            return valueFromRetentionForm(cashflowEntity);
        }
        return " ";
    }

    private String valueFromRetentionForm(CashflowEntity entity) {
        boolean isBankGuarantee = StringUtils.equals(RetentionFormEnum.BANK_GUARANTEE.getLabel(), entity.getForm());
        boolean isInsuranceBond = StringUtils.equals(RetentionFormEnum.INSURANCE_BOND.getLabel(), entity.getForm());
        boolean isOutBalance = StringUtils.equals("Out of Balance", entity.getForm());
        return isBankGuarantee ? "B" : (isInsuranceBond ? "I" : (isOutBalance ? "O" : " "));
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
        return " ";
    }

    public void convertToCsv(String sourceFile, String destinationFolder) {
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
        catch (Exception ex) {
            log.info("Caught an: " + ex.getClass().getName());
            log.info("Message: " + ex.getMessage());
            log.info("Stacktrace follows:.....");
            ex.printStackTrace(System.out);
        }
    }

    public void deleteFileTemporal(String filePath) {
        try {
            File file = new File(filePath);
            if (file.delete()) {
                log.info(file.getName() + " is deleted!");
            } else {
                log.info("Delete operation is failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPagePackageMilestone(final List<PurchaseOrderEntity> list) {
        processor.createSpreadsheet("PkgScInf");
        generateSpreadsheetCashflowDetail(list);
    }

    private void generateSpreadsheetCashflowDetail(List<PurchaseOrderEntity> list) {
        for (PurchaseOrderEntity entity : list) {
            List<CashflowDetailEntity> cashflowDetailList = cashflowService.findOrderedByCurrencyAndItem(entity.getPurchaseOrderProcurementEntity().getCashflow().getId());
            if (!cashflowDetailList.isEmpty()) {
                processor.createRow(rowNoMilestone);
                for (CashflowDetailEntity cf : cashflowDetailList) {
                    processor.createRow(rowNoMilestone);
                    fillingContentCashflowDetail(entity, cf);
                    rowNoMilestone++;
                }
            }
        }
    }

    private void fillingContentCashflowDetail(PurchaseOrderEntity entity, CashflowDetailEntity cashflowDetail) {
        Util util = new Util();
        util.setConfiguration(configuration);
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimalWithoutComma());
        processor.writeStringValue(0, entity.getProjectEntity().getTitle() != null ? entity.getProjectEntity().getTitle() : " ");
        processor.writeStringValue(1, entity.getPo() != null ? entity.getPo() : " ");
        processor.writeStringValue(2, cashflowDetail.getItem() != null ? cashflowDetail.getItem() : " ");
        processor.writeStringValue(3, cashflowDetail.getMilestone() != null ? cashflowDetail.getMilestone() : " ");
        processor.writeStringValue(4, cashflowDetail.getProjectCurrency() != null ? cashflowDetail.getProjectCurrency().getCurrency().getCode() : " ");
        processor.writeStringValue(5, cashflowDetail.getOrderAmt() != null ? decFormat.format(cashflowDetail.getOrderAmt()) : " ");
        processor.writeStringValue(6, cashflowDetail.getPaymentDate() != null ? configuration.convertDateToExportFileCsv(cashflowDetail.getPaymentDate()) : " ");
    }

    private String generateFileName(String fileNameSource, String folderName) {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
        String dateStr = format.format(new Date());
        String fileName = folderName+" - "+dateStr.toUpperCase() + " - " + fileNameSource;
        return fileName;
    }

}

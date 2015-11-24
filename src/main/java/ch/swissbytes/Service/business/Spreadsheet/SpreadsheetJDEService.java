package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Purchase;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.SpreadsheetProcessor;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 21/07/2015.
 */
public class SpreadsheetJDEService implements Serializable {

    public static final Logger log = Logger.getLogger(SpreadsheetJDEService.class.getName());

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


    public void generateWorkbookToExport(final List<PurchaseOrderEntity> list, String folderName) {
        rowNo = 2;
        rowNoMilestone = 2;
        String pathJDE = System.getProperty("fqmes.path.export.jde");
        pathJDE = pathJDE.replace("{project_field}", folderName);
        processWorkbook(list);
        log.info("written JDE successfully...");
        processor.doSaveWorkBook(pathJDE, generateFileName());
    }

    public InputStream generateWorkbook(final List<PurchaseOrderEntity> list) {
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
        createPagePackageHeader(list);
        createPagePackageMilestone(list);
    }

    private void createPagePackageMilestone(final List<PurchaseOrderEntity> list) {
        processor.createSpreadsheet("PkgScInf");
        createHeaderCMS(list.get(0));
        createHeaderMilestone();
        generateSpreadsheetCashflowDetail(list);
    }


    private void createPagePackageHeader(final List<PurchaseOrderEntity> list) {
        processor.createSpreadsheet("PkgHdr");
        createHeaderCMS(list.get(0));
        createHeaderPO();
        generateSpreadsheetPurchaseOrderDetail(list);
    }

    private void createHeaderCMS(PurchaseOrderEntity entity) {
        processor.createRow(0);
        processor.writeStringBoldValue(0, "PROJECT: ");
        processor.writeStringValue(1, entity.getProjectEntity().getProjectNumber());
        processor.writeStringBoldValue(5, "EXPORT TO CMS");
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
                for (CashflowDetailEntity cf : cashflowDetailList){
                    processor.createRow(rowNoMilestone);
                    prepareDetailContentCashflowDetail(entity, cf);
                    rowNoMilestone++;
                }
            }
        }
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list) {
        for (PurchaseOrderEntity entity : list) {
            BigDecimal totalForCurrency = new BigDecimal("0.00000").setScale(5, RoundingMode.CEILING);
            List<ItemEntity> itemEntityList = scopeSupplyService.getItemsOrderedByCurrency(entity.getId());
            if (!itemEntityList.isEmpty()) {
                boolean hasOneItem = itemEntityList.size() == 1;
                processor.createRow(rowNo);
                ItemEntity item = itemEntityList.get(0);
                ProjectCurrencyEntity currentCurrency = item.getProjectCurrency();
                int itemListSize = itemEntityList.size();
                prepareFirstLineContent(entity, item, hasOneItem);
                itemEntityList.remove(0);
                rowNo++;
                if (item.getTotalCost() != null) {
                    totalForCurrency = totalForCurrency.add(item.getTotalCost());
                }
                boolean hasOnlyOneCurrency = true;
                for (ItemEntity ss : itemEntityList) {
                    if (ss.getTotalCost() != null && currentCurrency != null && ss.getProjectCurrency() != null) {
                        if (currentCurrency.getId().longValue() == ss.getProjectCurrency().getId().longValue()) {
                            totalForCurrency = totalForCurrency.add(ss.getTotalCost());
                            processor.createRow(rowNo);
                            prepareDetailContent(entity, ss);
                            rowNo++;
                        } else {
                            hasOnlyOneCurrency = false;
                            currentCurrency = ss.getProjectCurrency();
                            createRowTotalPrice(entity, totalForCurrency);
                            totalForCurrency = ss.getTotalCost();
                            rowNo++;
                            processor.createRow(rowNo);
                            prepareDetailContent(entity, ss);
                            rowNo++;
                        }
                    }
                    if (!hasOnlyOneCurrency && ss.getId().longValue() == itemEntityList.get(itemEntityList.size() - 1).getId().longValue()) {
                        createRowTotalPrice(entity, totalForCurrency);
                        rowNo++;
                    }
                }
                if (hasOnlyOneCurrency && itemListSize > 1) {
                    createRowTotalPrice(entity, totalForCurrency);
                    rowNo++;
                }
            }
        }
    }

    private void createRowTotalPrice(PurchaseOrderEntity entity, BigDecimal totalForCurrency) {
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.createRow(rowNo);
        processor.writeStringBoldValue(9, entity.getPo().toUpperCase() + "v" + entity.getVariation() + " TOTAL");
        processor.writeStringBoldValue(10, totalForCurrency != null ? decFormat.format(totalForCurrency) : "");
        processor.writeStringValue(14, entity.getPurchaseOrderProcurementEntity().getCashflow().getApplyRetention() ? "Yes" : "No");
        processor.writeStringValue(15, entity.getPurchaseOrderProcurementEntity().getCashflow().getApplyRetentionSecurityDeposit() ? "Yes" : "No");
    }

    private void prepareFirstLineContentCashflowDetail(PurchaseOrderEntity entity, CashflowDetailEntity cashflowDetail, boolean hasOneMilestone) {
        Util util = new Util();
        util.setConfiguration(configuration);
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(0, entity.getPo() != null ? entity.getPo() : "");
        processor.writeStringValue(1, entity.getVariation() != null ? entity.getVariation() : "");
        processor.writeStringValue(2, cashflowDetail.getItem() != null ? cashflowDetail.getItem() : "");
        processor.writeStringValue(3, cashflowDetail.getMilestone() != null ? cashflowDetail.getMilestone() : "");
        processor.writeStringValue(4, cashflowDetail.getProjectCurrency() != null ? cashflowDetail.getProjectCurrency().getCurrency().getCode() : "");
        processor.writeStringValue(5, cashflowDetail.getOrderAmt() != null ? decFormat.format(cashflowDetail.getOrderAmt()) : "");
        processor.writeStringValue(6, cashflowDetail.getPaymentDate() != null ? configuration.convertDateToExportFile(cashflowDetail.getPaymentDate()) : "");
    }

    private void prepareFirstLineContent(PurchaseOrderEntity entity, ItemEntity item, boolean hasOneItem) {
        Util util = new Util();
        util.setConfiguration(configuration);
        processor.writeStringValue(0, entity.getPo() != null ? entity.getPo() : "");
        processor.writeStringValue(1, entity.getVariation() != null ? entity.getVariation() : "");
        processor.writeStringValue(2, entity.getPurchaseOrderProcurementEntity().getOrderDate() != null ? configuration.convertDateToExportFile(entity.getPurchaseOrderProcurementEntity().getOrderDate()) : "");
        processor.writeStringValue(3, entity.getPurchaseOrderProcurementEntity().getSupplier() != null ? entity.getPurchaseOrderProcurementEntity().getSupplier().getCompany() : "");
        processor.writeStringValue(4, item.getCode() != null ? item.getCode() : "");
        processor.writeStringValue(5, item.getDescription() != null ? item.getDescription() : "");
        processor.writeStringValue(6, item.getProjectCurrency() != null ? item.getProjectCurrency().getCurrency().getCode() : "");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(7, item.getCost() != null ? decFormat.format(item.getCost()) : "");
        processor.writeStringValue(8, item.getUnit() != null ? item.getUnit() : "");
        processor.writeStringValue(9, item.getQuantity() != null ? item.getQuantity().toString() : "");
        processor.writeStringValue(10, item.getTotalCost() != null ? decFormat.format(item.getTotalCost()) : "");
        processor.writeStringValue(11, (entity.getPurchaseOrderProcurementEntity().getCashflow() != null && entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms() != null) ? entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms().getLabel() : "");
        processor.writeStringValue(12, item.getCostCode() != null ? item.getCostCode() : "");
        if (hasOneItem) {
            rowNo++;
            processor.createRow(rowNo);
            processor.writeStringBoldValue(9, entity.getPo().toUpperCase() + "v" + entity.getVariation() + " TOTAL");
            processor.writeStringBoldValue(10, item.getTotalCost() != null ? decFormat.format(item.getTotalCost()) : "");
            processor.writeStringValue(14, entity.getPurchaseOrderProcurementEntity().getCashflow().getApplyRetention() ? "Yes" : "No");
            processor.writeStringValue(15, entity.getPurchaseOrderProcurementEntity().getCashflow().getApplyRetentionSecurityDeposit() ? "Yes" : "No");
        }
    }

    private void prepareDetailContentCashflowDetail(PurchaseOrderEntity entity, CashflowDetailEntity cashflowDetail) {
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(2, cashflowDetail.getItem() != null ? cashflowDetail.getItem() : "");
        processor.writeStringValue(3, cashflowDetail.getMilestone() != null ? cashflowDetail.getMilestone() : "");
        processor.writeStringValue(4, cashflowDetail.getProjectCurrency() != null ? cashflowDetail.getProjectCurrency().getCurrency().getCode() : "");
        processor.writeStringValue(5, cashflowDetail.getOrderAmt() != null ? decFormat.format(cashflowDetail.getOrderAmt()) : "");
        processor.writeStringValue(6, cashflowDetail.getPaymentDate() != null ? configuration.convertDateToExportFile(cashflowDetail.getPaymentDate()) : "");
    }

    private void prepareDetailContent(PurchaseOrderEntity entity, ItemEntity item) {
        processor.writeStringValue(0, "");
        processor.writeStringValue(1, "");
        processor.writeStringValue(2, "");
        processor.writeStringValue(3, "");
        processor.writeStringValue(4, item.getCode() != null ? item.getCode() : "");
        processor.writeStringValue(5, item.getDescription() != null ? item.getDescription() : "");
        processor.writeStringValue(6, item.getProjectCurrency() != null ? item.getProjectCurrency().getCurrency().getCode() : "");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(7, item.getCost() != null ? decFormat.format(item.getCost()) : "");
        processor.writeStringValue(8, item.getUnit() != null ? item.getUnit() : "");
        processor.writeStringValue(9, item.getQuantity() != null ? item.getQuantity().toString() : "");
        processor.writeStringValue(10, item.getTotalCost() != null ? decFormat.format(item.getTotalCost()) : "");
        processor.writeStringValue(11, (entity.getPurchaseOrderProcurementEntity().getCashflow() != null && entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms() != null) ? entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms().getLabel() : "");
        processor.writeStringValue(12, item.getCostCode() != null ? item.getCostCode() : "");
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

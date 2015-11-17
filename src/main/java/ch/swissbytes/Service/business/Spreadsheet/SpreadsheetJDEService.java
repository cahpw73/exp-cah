package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.fqmes.util.Configuration;
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
    private Configuration configuration;
    @Inject
    private CashflowService cashflowService;
    @Inject
    private ScopeSupplyService scopeSupplyService;
    @Inject
    private Util util;

    public SpreadsheetProcessor processor;

    int rowNo = 2;

    public void generateWorkbookToExport(final List<PurchaseOrderEntity> list, String folderName) {
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
        String fileName = dateStr + "Commitemnts.xlsx";
        return fileName;
    }

    public void processWorkbook(final List<PurchaseOrderEntity> list) {
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        processor.createSpreadsheet("PkgHdr");
        createHeaderCMS(list.get(0));
        createHeaderPO();
        generateSpreadsheetPurchaseOrderDetail(list);
    }

    private void createHeaderCMS(PurchaseOrderEntity entity) {
        processor.createRow(0);
        processor.writeStringValue(0, "PROJECT: ");
        processor.writeStringValue(1, entity.getProjectEntity().getProjectNumber());
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
        DecimalFormat decFormat = new DecimalFormat(new Configuration().getPatternDecimal());
        processor.createRow(rowNo);
        processor.writeStringValue(9, entity.getPo().toUpperCase() + " TOTAL");
        processor.writeStringValue(10, totalForCurrency != null ? decFormat.format(totalForCurrency) : "");
    }

    private void prepareFirstLineContent(PurchaseOrderEntity entity, ItemEntity item, boolean hasOneItem) {
        Util util = new Util();
        Configuration configuration = new Configuration();
        util.setConfiguration(configuration);
        processor.writeStringValue(0, entity.getPo() != null ? entity.getPo() : "");
        processor.writeStringValue(1, entity.getVariation() != null ? entity.getVariation() : "");
        processor.writeStringValue(2, entity.getPurchaseOrderProcurementEntity().getOrderDate() != null ? util.toLocal(entity.getPurchaseOrderProcurementEntity().getOrderDate()) : "");
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
            processor.writeStringValue(9, entity.getPo().toUpperCase() + " TOTAL");
            processor.writeStringValue(10, item.getTotalCost() != null ? decFormat.format(item.getTotalCost()) : "");
        }
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
        processor.writeStringValue(0, "PO No.");
        processor.writeStringValue(1, "Var");
        processor.writeStringValue(2, "PO Date");
        processor.writeStringValue(3, "Supplier");
        processor.writeStringValue(4, "Item");
        processor.writeStringValue(5, "Description");
        processor.writeStringValue(6, "Currency");
        processor.writeStringValue(7, "Unit Price");
        processor.writeStringValue(8, "UOM");
        processor.writeStringValue(9, "Qty");
        processor.writeStringValue(10, "Total Price");
        processor.writeStringValue(11, "Terms");
        processor.writeStringValue(12, "Cost Code");
        processor.writeStringValue(13, "Schedule E");
    }

}

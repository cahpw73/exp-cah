package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.CreateEmailSender;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.SpreadsheetProcessor;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 21/07/2015.
 */
public class SpreadsheetService implements Serializable {

    public static final Logger log = Logger.getLogger(SpreadsheetService.class.getName());

    @Inject
    private ScopeSupplyDao supplyDao;
    @Inject
    private CashflowService cashflowService;
    @Inject
    private ScopeSupplyService scopeSupplyService;
    @Inject
    public SpreadsheetProcessor processor;

    public Configuration configuration = new Configuration();

    int rowNo;

    public void generateWorkbookToExport(final List<PurchaseOrderEntity> list, String folderName) throws IOException {
        rowNo = 2;
        String pathCMS = System.getProperty("fqmes.path.export.cms");
        pathCMS = pathCMS.replace("{project_field}", folderName);
        processWorkbook(list);
        log.info("written CMS successfully...");
        processor.doSaveWorkBook(pathCMS, generateFileName());
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
        processor.createSpreadsheet("PkgHdr");
        createHeaderCMS(list.get(0));
        createHeaderPO();
        generateSpreadsheetPurchaseOrderDetail(list);
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
                            prepareDetailContent(ss);
                            rowNo++;
                        } else {
                            hasOnlyOneCurrency = false;
                            currentCurrency = ss.getProjectCurrency();
                            createRowTotalPrice(entity, totalForCurrency);
                            totalForCurrency = ss.getTotalCost();
                            rowNo++;
                            processor.createRow(rowNo);
                            prepareDetailContent(ss);
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
        processor.writeStringBoldValue(12, entity.getPo().toUpperCase() + "v" + entity.getVariation() + " TOTAL");
        processor.writeStringBoldValue(13, totalForCurrency != null ? decFormat.format(totalForCurrency) : "");
    }

    private void prepareFirstLineContent(PurchaseOrderEntity entity, ItemEntity item, boolean hasOneItem) {
        Util util = new Util();
        util.setConfiguration(configuration);
        processor.writeStringValue(0, entity.getPurchaseOrderProcurementEntity().getClazz() != null ? entity.getPurchaseOrderProcurementEntity().getClazz().getLabel() : "");
        processor.writeStringValue(1, entity.getPo() != null ? entity.getPo() : "");
        processor.writeStringValue(2, entity.getVariation() != null ? entity.getVariation() : "");
        processor.writeStringValue(3, entity.getPurchaseOrderProcurementEntity().getOrderDate() != null ? configuration.convertDateToExportFile(entity.getPurchaseOrderProcurementEntity().getOrderDate()) : "");
        processor.writeStringValue(4, entity.getPoTitle() != null ? entity.getPoTitle() : "");
        processor.writeStringValue(5, entity.getPurchaseOrderProcurementEntity().getSupplier() != null ? entity.getPurchaseOrderProcurementEntity().getSupplier().getCompany() : "");
        processor.writeStringValue(6, item.getCode() != null ? item.getCode() : "");
        processor.writeStringValue(7, item.getCostCode() != null ? item.getCostCode() : "");
        processor.writeStringValue(8, item.getDescription() != null ? item.getDescription() : "");
        processor.writeStringValue(9, item.getProjectCurrency() != null ? item.getProjectCurrency().getCurrency().getCode() : "");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(10, item.getCost() != null ? decFormat.format(item.getCost()) : "");
        processor.writeStringValue(11, item.getUnit() != null ? item.getUnit() : "");
        processor.writeStringValue(12, item.getQuantity() != null ? item.getQuantity().toString() : "");
        processor.writeStringValue(13, item.getTotalCost() != null ? decFormat.format(item.getTotalCost()) : "");
        if (hasOneItem) {
            rowNo++;
            processor.createRow(rowNo);
            processor.writeStringBoldValue(12, entity.getPo().toUpperCase() + " TOTAL");
            processor.writeStringBoldValue(13, item.getTotalCost() != null ? decFormat.format(item.getTotalCost()) : "");
        }
    }

    private void prepareDetailContent(ItemEntity ss) {
        processor.writeStringValue(0, "");
        processor.writeStringValue(1, "");
        processor.writeStringValue(2, "");
        processor.writeStringValue(3, "");
        processor.writeStringValue(4, "");
        processor.writeStringValue(5, "");
        processor.writeStringValue(6, ss.getCode() != null ? ss.getCode() : "");
        processor.writeStringValue(7, ss.getCostCode() != null ? ss.getCostCode() : "");
        processor.writeStringValue(8, ss.getDescription() != null ? ss.getDescription() : "");
        processor.writeStringValue(9, ss.getProjectCurrency() != null ? ss.getProjectCurrency().getCurrency().getCode() : "");
        DecimalFormat decFormat = new DecimalFormat(new Configuration().getPatternDecimal());
        processor.writeStringValue(10, ss.getCost() != null ? decFormat.format(ss.getCost()) : "");
        processor.writeStringValue(11, ss.getUnit() != null ? ss.getUnit() : "");
        processor.writeStringValue(12, ss.getQuantity() != null ? ss.getQuantity().toString() : "");
        processor.writeStringValue(13, ss.getTotalCost() != null ? decFormat.format(ss.getTotalCost()) : "");
    }

    private void createHeaderCMS(PurchaseOrderEntity entity) {
        processor.createRow(0);
        processor.writeStringBoldValue(0, "PROJECT: ");
        processor.writeStringValue(1, entity.getProjectEntity().getProjectNumber());
        processor.writeStringBoldValue(5, "EXPORT TO CMS");
        processor.writeStringBoldValue(6, "DATE:");
        processor.writeStringValue(7, configuration.convertDateToExportFile(new Date()));
    }

    private void createHeaderPO() {
        processor.createRow(1);
        processor.writeStringBoldValue(0, "Class");
        processor.writeStringBoldValue(1, "PO No.");//*
        processor.writeStringBoldValue(2, "Var");
        processor.writeStringBoldValue(3, "PO Date");//*
        processor.writeStringBoldValue(4, "Title");//*
        processor.writeStringBoldValue(5, "Supplier");
        processor.writeStringBoldValue(6, "Item");//*
        processor.writeStringBoldValue(7, "Cost Code");
        processor.writeStringBoldValue(8, "Description");
        processor.writeStringBoldValue(9, "Currency");
        processor.writeStringBoldValue(10, "Unit Price");//*
        processor.writeStringBoldValue(11, "UOM");
        processor.writeStringBoldValue(12, "Qty");
        processor.writeStringBoldValue(13, "Total Price");//*
    }

}

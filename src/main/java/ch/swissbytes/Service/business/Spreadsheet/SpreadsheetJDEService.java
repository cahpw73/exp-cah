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

    public InputStream generateWorkbook(final List<PurchaseOrderEntity> list) {
        log.info("purchaseOrder list size: " + list.size());
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        processor.createSpreadsheet("PkgHdr");
        createHeaderCMS(list.get(0));
        createHeaderPO();
        generateSpreadsheetPurchaseOrderDetail(list);
        return processor.getContentSheet();

    }

    private void createHeaderCMS(PurchaseOrderEntity entity){
        processor.createRow(0);
        processor.writeStringValue(0, "PROJECT: ");
        processor.writeStringValue(1, entity.getProjectEntity().getProjectNumber());
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list) {
        int rowNo = 2;
        processor.createRow(rowNo);
        for (PurchaseOrderEntity entity : list) {
            List<ItemEntity> itemEntityList = scopeSupplyService.getItemsBYPOId(entity.getId());
            if (!itemEntityList.isEmpty()) {
                processor.writeStringValue(0, entity.getPo()!=null?entity.getPo():"");
                processor.writeStringValue(1, entity.getVariation()!=null?entity.getVariation():"");
                processor.writeStringValue(2, entity.getPurchaseOrderProcurementEntity().getOrderDate()!=null?util.toLocal(entity.getPurchaseOrderProcurementEntity().getOrderDate()):"");
                processor.writeStringValue(3, entity.getPurchaseOrderProcurementEntity().getSupplier()!=null?entity.getPurchaseOrderProcurementEntity().getSupplier().getFullName():"");
                ItemEntity item = itemEntityList.get(0);
                processor.writeStringValue(4, item.getCode()!=null?item.getCode():"");
                processor.writeStringValue(5, item.getDescription()!=null?item.getDescription():"");
                processor.writeStringValue(6, item.getProjectCurrency()!=null?item.getProjectCurrency().getCurrency().getCode():"");
                DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
                processor.writeStringValue(7, item.getCost()!=null? decFormat.format(item.getCost()):"");
                processor.writeStringValue(8, item.getUnit()!=null?item.getUnit():"");
                processor.writeStringValue(9, item.getQuantity()!=null? item.getQuantity().toString():"");
                processor.writeStringValue(10, item.getTotalCost()!=null? decFormat.format(item.getTotalCost()):"");
                processor.writeStringValue(11, entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms()!=null? entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms().getLabel():"");
                processor.writeStringValue(5, item.getCostCode()!=null?item.getCostCode():"");
                itemEntityList.remove(0);
                rowNo++;
                for (ItemEntity ss : itemEntityList) {
                    processor.createRow(rowNo);
                    prepareDetailContent(entity, ss);

                    rowNo++;
                }
            }
        }
    }

    private void prepareDetailContent(PurchaseOrderEntity entity, ItemEntity item) {
        processor.writeStringValue(0, "");
        processor.writeStringValue(1, "");
        processor.writeStringValue(2, "");
        processor.writeStringValue(3, "");
        processor.writeStringValue(4, item.getCode()!=null?item.getCode():"");
        processor.writeStringValue(5, item.getDescription()!=null?item.getDescription():"");
        processor.writeStringValue(6, item.getProjectCurrency()!=null?item.getProjectCurrency().getCurrency().getCode():"");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(7, item.getCost()!=null? decFormat.format(item.getCost()):"");
        processor.writeStringValue(8, item.getUnit()!=null?item.getUnit():"");
        processor.writeStringValue(9, item.getQuantity()!=null? item.getQuantity().toString():"");
        processor.writeStringValue(10, item.getTotalCost()!=null? decFormat.format(item.getTotalCost()):"");
        processor.writeStringValue(11, entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms()!=null? entity.getPurchaseOrderProcurementEntity().getCashflow().getPaymentTerms().getLabel():"");
        processor.writeStringValue(5, item.getCostCode()!=null?item.getCostCode():"");
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

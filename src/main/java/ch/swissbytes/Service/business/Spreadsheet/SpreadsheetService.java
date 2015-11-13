package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        /*generateSpreadsheetPurchaseOrder(list);*/
        log.info("written successfully...");
        return processor.getContentSheet();

    }

    private void generateSpreadsheetPurchaseOrder(final List<PurchaseOrderEntity> list) {
        processor.createSpreadsheet("PkgHdr");
        // createHeaderPO(processor);
        int rowNo = 0;
        for (PurchaseOrderEntity purchaseOrder : list) {
            processor.createRow(rowNo);
            prepareHeaderContent(purchaseOrder);
            rowNo++;
        }
    }

    private void prepareHeaderContent(PurchaseOrderEntity purchaseOrder) {
        //Package Type Code
        processor.writeStringValue(0, "");
        //Package Number
        processor.writeStringValue(1, purchaseOrder.getPo());
        //Package Amendment
        processor.writeStringValue(2, purchaseOrder.getVariation());
        //Award Date
        processor.writeStringValue(3, getAwardDate(purchaseOrder));
        //Package Title.
        processor.writeStringValue(4, purchaseOrder.getPoTitle());
        //Contractor Vendor Name
        processor.writeStringValue(5, getPaymentTerm(purchaseOrder));
        //Payment Terms Notes
        processor.writeStringValue(6, "");
        //CER/CAR Number
        processor.writeStringValue(7, "");
        //Responsibility
        processor.writeStringValue(8, "");
        //SAP Cross Reference
        processor.writeStringValue(9, "");
        //Pkg Default Currency Code
        processor.writeStringValue(10, "");
    }

    private String getPaymentTerm(PurchaseOrderEntity purchaseOrder) {
        List<CashflowEntity> cashflowList = cashflowService.findByPoId(purchaseOrder.getPurchaseOrderProcurementEntity().getId());
        if (!cashflowList.isEmpty()) {
            CashflowEntity cashflowEntity = cashflowList.get(0);
            if (cashflowEntity.getPaymentTerms() != null) {
                return cashflowEntity.getPaymentTerms().getLabel();
            }
        }
        return "";
    }

    private String getAwardDate(PurchaseOrderEntity purchaseOrder) {
        if (purchaseOrder.getPoDeliveryDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(configuration.getFormatDate());
            return formatter.format(purchaseOrder.getPoDeliveryDate());
        }
        return "";
    }

    private void prepareDetailContent(PurchaseOrderEntity entity, ItemEntity ss) {
       /* firstSide(entity, ss);
        secondSide(10, entity, ss);*/
        processor.writeStringValue(0, entity.getPurchaseOrderProcurementEntity().getClazz()!=null?entity.getPurchaseOrderProcurementEntity().getClazz().getLabel():"");
        processor.writeStringValue(1, entity.getPo()!=null?entity.getPo():"");
        processor.writeStringValue(2, entity.getVariation()!=null?entity.getVariation():"");
        processor.writeStringValue(3, entity.getPurchaseOrderProcurementEntity().getOrderDate()!=null?util.toLocal(entity.getPurchaseOrderProcurementEntity().getOrderDate()):"") ;
        processor.writeStringValue(4, entity.getPoTitle()!=null?entity.getPoTitle():"");
        processor.writeStringValue(5, entity.getPurchaseOrderProcurementEntity().getSupplier()!=null?entity.getPurchaseOrderProcurementEntity().getSupplier().getFullName():"");
        processor.writeStringValue(6, ss.getCode()!=null?ss.getCode():"");
        processor.writeStringValue(7, ss.getCostCode()!=null?ss.getCostCode():"");
        processor.writeStringValue(8, ss.getDescription()!=null?ss.getDescription():"");
        processor.writeStringValue(9, ss.getProjectCurrency()!=null?ss.getProjectCurrency().getCurrency().getCode():"");
        DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
        processor.writeStringValue(10, ss.getCost()!=null? decFormat.format(ss.getCost()):"");
        processor.writeStringValue(11, ss.getUnit()!=null?ss.getUnit():"");
        processor.writeStringValue(12, ss.getQuantity()!=null? ss.getQuantity().toString():"");
        processor.writeStringValue(13, ss.getTotalCost()!=null? decFormat.format(ss.getTotalCost()):"");
    }

    private void prepareDetailContentMultiCurrencies(PurchaseOrderEntity entity, ItemEntity ss) {
        firstSide(entity, ss);
        //PayItemCurrency
        processor.writeStringValue(10, ss.getProjectCurrency().getCurrency().getCode());
        secondSide(11, entity, ss);
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list) {
        int rowNo = 2;
        processor.createRow(rowNo);
        for (PurchaseOrderEntity entity : list) {
            List<ItemEntity> itemEntityList = scopeSupplyService.getItemsBYPOId(entity.getId());
            if (!itemEntityList.isEmpty()) {
                processor.writeStringValue(0, entity.getPurchaseOrderProcurementEntity().getClazz()!=null?entity.getPurchaseOrderProcurementEntity().getClazz().getLabel():"");
                processor.writeStringValue(1, entity.getPo()!=null?entity.getPo():"");
                processor.writeStringValue(2, entity.getVariation()!=null?entity.getVariation():"");
                processor.writeStringValue(3, entity.getPurchaseOrderProcurementEntity().getOrderDate()!=null?util.toLocal(entity.getPurchaseOrderProcurementEntity().getOrderDate()):"") ;
                processor.writeStringValue(4, entity.getPoTitle()!=null?entity.getPoTitle():"");
                processor.writeStringValue(5, entity.getPurchaseOrderProcurementEntity().getSupplier()!=null?entity.getPurchaseOrderProcurementEntity().getSupplier().getFullName():"");
                ItemEntity item = itemEntityList.get(0);
                processor.writeStringValue(6, item.getCode()!=null?item.getCode():"");
                processor.writeStringValue(7, item.getCostCode()!=null?item.getCostCode():"");
                processor.writeStringValue(8, item.getDescription()!=null?item.getDescription():"");
                processor.writeStringValue(9, item.getProjectCurrency()!=null?item.getProjectCurrency().getCurrency().getCode():"");
                DecimalFormat decFormat = new DecimalFormat(configuration.getPatternDecimal());
                processor.writeStringValue(10, item.getCost()!=null? decFormat.format(item.getCost()):"");
                processor.writeStringValue(11, item.getUnit()!=null?item.getUnit():"");
                processor.writeStringValue(12, item.getQuantity()!=null? item.getQuantity().toString():"");
                processor.writeStringValue(13, item.getTotalCost()!=null? decFormat.format(item.getTotalCost()):"");

                /*boolean hasMultiCurrencies = verifyMultiCurrenciesByScopeSupply(itemEntityList);*/
                itemEntityList.remove(0);
                rowNo++;
                for (ItemEntity ss : itemEntityList) {
                    processor.createRow(rowNo);
                    /*if (hasMultiCurrencies) {
                        prepareDetailContentMultiCurrencies(entity, ss);
                    } else {*/
                        prepareDetailContent(entity, ss);
                    //}
                    rowNo++;
                }
            }
        }
    }

    private void firstSide(PurchaseOrderEntity entity, ItemEntity ss) {
        //Package Number
        processor.writeStringValue(0, entity.getPo());
        // Package Amendment
        processor.writeStringValue(1, entity.getVariation());
        //WA/Release
        processor.writeStringValue(2, "");
        //Item Number
        processor.writeStringValue(3, ss.getCode());
        //Facility Code
        processor.writeStringValue(4, "");
        //Trade Commodity Code
        processor.writeStringValue(5, "");
        //Phase Code
        processor.writeStringValue(6, "");
        //Item Description
        processor.writeStringValue(7, ss.getDescription());
        //PayItem Group
        processor.writeStringValue(8, "");
        //Pay Item Payment Type
        processor.writeStringValue(9, "");
    }

    private void secondSide(int start, PurchaseOrderEntity entity, ItemEntity ss) {
        //Unit Price
        int col=start;
        processor.writeDoubleValue(col, ss.getCost() != null ? ss.getCost().doubleValue() : null);
        //Unit of Measure
        processor.writeStringValue(col++, ss.getUnit());
        //Committed Qty
        processor.writeStringValue(col++, "");
        //Committed Install Hrs.
        processor.writeStringValue(col++, "");
        //Committed Costs.
        processor.writeStringValue(col++, "");
        //Forecast Quantity
        processor.writeStringValue(col++, "");
        //Forecast Install Hrs.
        processor.writeStringValue(col++, "");
        //Forecast Costs
        processor.writeStringValue(col++, "");
        //Sort Select Code 01
        processor.writeStringValue(col++, "");
        //Sort Select Code 02
        processor.writeStringValue(col++, "");
        //Sort Select  Code 03
        processor.writeStringValue(col++, "");
        //Sort Select Code 04
        processor.writeStringValue(col++, "");
        //User Select 01
        processor.writeStringValue(col++, "");
        //User Select 02
        processor.writeStringValue(col++, "");
        //User Select 03
        processor.writeStringValue(col++, "");
        //User Select 04
        processor.writeStringValue(col++, "");
    }

    private boolean verifyMultiCurrenciesByScopeSupply(List<ItemEntity> itemEntityList) {
        List<ProjectCurrencyEntity> currencies = new ArrayList<>();
        for (ItemEntity ss : itemEntityList) {
            if (ss.getProjectCurrency() != null && !currencies.contains(ss.getProjectCurrency())) {
                currencies.add(ss.getProjectCurrency());
            }
        }
        return currencies.size() > 1;
    }

    private void createHeaderCMS(PurchaseOrderEntity entity){
        processor.createRow(0);
        processor.writeStringValue(0, "PROJECT: ");
        processor.writeStringValue(1, entity.getProjectEntity().getProjectNumber());
    }

    private void createHeaderPO() {
        processor.createRow(1);
        processor.writeStringValue(0, "Class");
        processor.writeStringValue(1, "PO No.");//*
        processor.writeStringValue(2, "Var");
        processor.writeStringValue(3, "PO Date");//*
        processor.writeStringValue(4, "Title");//*
        processor.writeStringValue(5, "Supplier");
        processor.writeStringValue(6, "Item");//*
        processor.writeStringValue(7, "Cost Code");
        processor.writeStringValue(8, "Description");
        processor.writeStringValue(9, "Currency");
        processor.writeStringValue(10, "Unit Price");//*
        processor.writeStringValue(11, "UOM");
        processor.writeStringValue(12, "Qty");
        processor.writeStringValue(13, "Total Price");//*
    }

    private void createHeaderPODetail(SpreadsheetProcessor sp) {
        sp.createRow(0);
        sp.writeStringValue(0, "PackageNumber");//*
        sp.writeStringValue(1, "PackageAmendment");
        sp.writeStringValue(2, "WA/Release");
        sp.writeStringValue(3, "Item Number");//*
        sp.writeStringValue(4, "FacilityCode");
        sp.writeStringValue(5, "TradeCommodityCode");
        sp.writeStringValue(6, "PhaseCode");
        sp.writeStringValue(7, "ItemDescription");//*
        sp.writeStringValue(8, "PayItemGroup");
        sp.writeStringValue(9, "PayItemPaymentType");
        sp.writeStringValue(10, "PayItemCurrency");
        sp.writeStringValue(11, "UnitPrice");//*
        sp.writeStringValue(12, "UnitOfMeasure");
        sp.writeStringValue(13, "CommittedQty");
        sp.writeStringValue(14, "CommittedInstallHrs");
        sp.writeStringValue(15, "CommittedCosts");
        sp.writeStringValue(16, "ForecastQty");
        sp.writeStringValue(17, "ForecastInstallHrs");
        sp.writeStringValue(18, "ForecastCosts");
        sp.writeStringValue(19, "SortSelectCode01");
        sp.writeStringValue(20, "SortSelectCode02");
        sp.writeStringValue(21, "SortSelectCode03");
        sp.writeStringValue(22, "SortSelectCode04");
    }

}

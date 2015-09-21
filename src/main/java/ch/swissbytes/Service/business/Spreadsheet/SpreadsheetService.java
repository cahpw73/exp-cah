package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Purchase;
import ch.swissbytes.procurement.util.SpreadsheetProcessor;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.Serializable;
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
    private Configuration configuration;
    @Inject
    private CashflowService cashflowService;
    @Inject
    private ScopeSupplyService scopeSupplyService;

    public SpreadsheetProcessor processor;

    public InputStream generateWorkbook(final List<PurchaseOrderEntity> list) {
        log.info("purchaseOrder list size: " + list.size());
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        generateSpreadsheetPurchaseOrder(list);
        generateSpreadsheetPurchaseOrderDetail(list);
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

    private void prepareDetailContent(PurchaseOrderEntity entity, ScopeSupplyEntity ss) {
        //Package Number
        processor.writeStringValue(0, entity.getPo());
        // Package Amendment
        processor.writeStringValue(1, entity.getVariation());
        //WA/Release
        processor.writeStringValue(2, "");
        //Item Number
        processor.writeStringValue(3, ss.getCode());
        //break;
        //Facility Code
        processor.writeStringValue(4, ss.getCode());
        //Trade Commodity Code
        processor.writeStringValue(5, "");
        //Phase Code
        processor.writeStringValue(6, "");
        //Item Description
        processor.writeStringValue(7, "");
        //PayItem Group
        processor.writeStringValue(8, "");
        //Pay Item Payment Type
        processor.writeStringValue(9, "");
        //Unit Price
        processor.writeStringValue(10, "");
        //Unit of Measure
        processor.writeDoubleValue(11, ss.getCost() != null ? ss.getCost().doubleValue() : null);
        //Committed Qty
        processor.writeStringValue(12, "");
        //Committed Install Hrs.
        processor.writeStringValue(13, "");
        //Committed Costs.
        processor.writeStringValue(14, "");
        //Forecast Quantity
        processor.writeStringValue(15, "");
        //Forecast Install Hrs.
        processor.writeStringValue(16, "");
        //Forecast Costs
        processor.writeStringValue(17, "");
        //Sort Select Code 01
        processor.writeStringValue(18, "");
        //Sort Select Code 02
        processor.writeStringValue(19, "");
        //Sort Select  Code 03
        processor.writeStringValue(20, "");
        //Sort Select Code 04
        processor.writeStringValue(21, "");
        //User Select 01
        processor.writeStringValue(22, "");
        //User Select 02
        processor.writeStringValue(23, "");
        //User Select 03
        processor.writeStringValue(24, "");
        //User Select 04
        processor.writeStringValue(25, "");
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list) {
        processor.createSpreadsheet("PkgDetail");
        // createHeaderPODetail(processor);
        int rowNo = 0;
        for (PurchaseOrderEntity entity : list) {
            List<ScopeSupplyEntity> scopeSupplyList = scopeSupplyService.scopeSupplyListByPOOId(entity.getId());
            for (ScopeSupplyEntity ss : scopeSupplyList) {
                processor.createRow(rowNo);
                prepareDetailContent(entity, ss);
                rowNo++;
            }
        }
    }

    private void createHeaderPO(SpreadsheetProcessor sp) {
        sp.createRow(0);
        sp.writeStringValue(0, "PackageTypeCode");
        sp.writeStringValue(1, "PackageNumber");//*
        sp.writeStringValue(2, "PackageAmendment");
        sp.writeStringValue(3, "AwardDate");//*
        sp.writeStringValue(4, "PackageTitle");//*
        sp.writeStringValue(5, "ContractorVendorName");
        sp.writeStringValue(6, "PaymentTermsNotes");//*
        sp.writeStringValue(7, "CER/CAR Number");
        sp.writeStringValue(8, "Responsibility");
        sp.writeStringValue(9, "SAP Cross Reference");
        sp.writeStringValue(10, "Pkg Default Currency Code");//*
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
        sp.writeStringValue(23, "UserSelect01");
        sp.writeStringValue(24, "UserSelect02");
        sp.writeStringValue(25, "UserSelect03");
        sp.writeStringValue(26, "UserSelect04");
    }

}

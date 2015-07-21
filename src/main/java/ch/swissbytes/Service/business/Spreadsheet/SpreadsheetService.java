package ch.swissbytes.Service.business.Spreadsheet;

import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.procurement.util.SpreadsheetProcessor;

import javax.inject.Inject;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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

    public void generatorWorkbook(final List<PurchaseOrderEntity> list, final String fileName) {
        log.info("purchaseOrder list size: " + list.size());
        processor = new SpreadsheetProcessor();
        processor.createWorkbook();
        generateSpreadsheetPurchaseOrder(list);
        generateSpreadsheetPurchaseOrderDetail(list);
        processor.saveWorkBook(fileName);
        log.info("written successfully...");
    }

    private void generateSpreadsheetPurchaseOrder(final List<PurchaseOrderEntity> list){
        processor.createSpreadsheet("PkgHdr");
        createHeaderPO(processor);
        int i = 0;
        int rowNo = 1;
        for (PurchaseOrderEntity entity : list) {
            processor.createRow(rowNo);
            while (i < 10) {
                switch (i) {
                    case 1:
                        processor.writeStringValue(i, entity.getPo());
                        break;
                    case 3:
                        if (entity.getPoDeliveryDate() != null) {
                            SimpleDateFormat formatter = new SimpleDateFormat(configuration.getFormatDate());
                            processor.writeStringValue(i, formatter.format(entity.getPoDeliveryDate()));
                        }
                        break;
                    case 4:
                        processor.writeStringValue(i, entity.getPoEntity().getOrderTitle());
                        break;
                    case 6:
                        List<CashflowEntity> cashflowList = cashflowService.findByPoId(entity.getPoEntity().getId());
                        if (!cashflowList.isEmpty()) {
                            CashflowEntity cashflowEntity = cashflowList.get(0);
                            if(cashflowEntity.getPaymentTerms() != null) {
                                processor.writeStringValue(i, cashflowEntity.getPaymentTerms().getLabel());
                            }
                        }
                        break;
                    case 10:
                        if (entity.getPoEntity().getCurrency() != null) {
                            processor.writeStringValue(i, entity.getPoEntity().getCurrency().getCurrency().getCode());
                        }
                        break;
                }
                i++;
            }
            i = 0;
            rowNo++;
        }
    }

    private void generateSpreadsheetPurchaseOrderDetail(final List<PurchaseOrderEntity> list){
        processor.createSpreadsheet("PkgDetail");
        createHeaderPODetail(processor);
        int i = 0;
        int rowNo = 1;
        for (PurchaseOrderEntity entity : list) {
            List<ScopeSupplyEntity> scopeSupplyList = scopeSupplyService.scopeSupplyListByPOOId(entity.getId());
            for (ScopeSupplyEntity ss: scopeSupplyList){
                processor.createRow(rowNo);
                while (i < 26) {
                    switch (i) {
                        case 0:
                            processor.writeStringValue(i, entity.getPo());
                            break;
                        case 3:
                            processor.writeStringValue(i, ss.getCode());
                            break;
                        case 7:
                            processor.writeStringValue(i, ss.getDescription());
                            break;
                        case 11:
                            if(ss.getCost()!=null){
                                processor.writeDoubleValue(i, ss.getCost().doubleValue());
                            }
                            break;
                    }
                    i++;
                }
                i = 0;
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

    private void createHeaderPODetail(SpreadsheetProcessor sp){
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

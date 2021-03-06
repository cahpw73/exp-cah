package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import com.itextpdf.text.DocumentException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 12/06/14.
 */
@Named
@ViewScoped
public class ReportProcBean implements Serializable {

    private Logger log = Logger.getLogger(ReportProcBean.class.getName());

    @PersistenceContext(unitName = "fqmPU")
    private EntityManager entityManager;

    @Resource(lookup = "java:/fqm/procurementDS")
    private DataSource dataSource;

    private Locale locale;

    private Map<String, String> messages;

    private Boolean openReport = false;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private Configuration configuration;

    @Inject
    private CashflowService cashflowService;

    public Boolean getOpenReport() {
        return openReport;
    }

    @PostConstruct
    public void init() {
        log.info("ReportBean init!");
    }


    @PreDestroy
    public void destroy() {
        log.info("Report Bean destroyed!");
    }

    public void printReportDeliverables(final PurchaseOrderEntity po, final Long projectId, final String termsPoNo) {
        log.info("public void printReportDeliverables()");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportDeliverables("/procurement/deliverables/reportDeliverables", "Procurement.Deliverables", messages, locale, configuration, po, projectId, termsPoNo,entityManager,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printReportExpediting(final PurchaseOrderEntity po, final Long projectId, final String termsPoNo) {
        log.info("public void printReportExpediting()");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportExpediting("/procurement/expediting/reportExpediting", "Procurement.Expediting", messages, locale, configuration, po, projectId, termsPoNo,entityManager,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printPurchaseOrder(final PurchaseOrderEntity po,List<ItemEntity> list,String preamble,List<ClausesEntity> clausesList,boolean draft,List<PODocumentEntity> poDocumentList) {
        log.info("printPurchaseOrder(purchaseOrderId[" + po.getId() + "])");
        openReport = false;
        PurchaseOrderEntity purchaseOrder=service.findById(po.getId());
        if(purchaseOrder!=null) {
            initializeParametersToJasperReport();
            List<CashflowEntity> cashflows = cashflowService.findByPoId(purchaseOrder.getPurchaseOrderProcurementEntity().getId());
            CashflowEntity  cashflowEntity=cashflows.size() > 0 ? cashflows.get(0) : null;
            if(cashflowEntity!=null){
                cashflowEntity.getCashflowDetailList().clear();
                cashflowEntity.getCashflowDetailList().addAll(cashflowService.findDetailByCashflowId(cashflowEntity.getId()));
            }
            String fileName = service.generateName(po);
            ReportView reportView = null;
            try {
                reportView = new ReportPurchaseOrder("/procurement/printPo/PrintPurchaseOrder", fileName.length() > 0 ? fileName : "Purchase Order",
                                                    messages, locale, configuration, purchaseOrder, list,dataSource, preamble, clausesList, cashflowEntity, entityManager,draft,poDocumentList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            reportView.printDocument(null);
        }
        openReport = true;
    }
    public void printProjectPurchaseOrder(final ProjectEntity project, final Map<String, Boolean> sortMap,Timestamp sortFrom,Timestamp sortTo) {
        log.info("printProjectPurchaseOrder");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportProjectProcurement("/procurement/projectProcurementReport/projectProcurementReport", "Procurement.project.purchase.order", messages, locale, configuration, project, sortMap,entityManager,dataSource,sortFrom,sortTo);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printPosRegister(final ProjectEntity project, final Map<String, Boolean> sortMap,final Map<String, Boolean> filterMap) {
        log.info("printPosRegister");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportPosRegister("/procurement/posRegisterReport/posRegisterReport", "Procurement.pos.register", messages, locale, configuration, project, sortMap,entityManager,filterMap,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printDetailedProcurementReport(final ProjectEntity project,final Map<String, Boolean> sortMap) {
        log.info("printDetailedProcurementReport");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportDetailedProcurement("/procurement/detailedProcurementReport/detailedProcurementReport", "Procurement.Detailed.Procurement", messages, locale, configuration, project, sortMap, dataSource);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printCommittedCurrenciesReport(final ProjectEntity project,final Map<String, Boolean> sortMap) {
        log.info("printCommittedCurrenciesReport");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportCommittedCurrencies("/procurement/committedCurrenciesReport/committedCurrenciesReport", "Procurement.Committed.Currencies", messages, locale, configuration,  project, sortMap,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }


    public void printBidderList(List<Long> suppliers, String packageNumber, String description, ProjectEntity project) {
        log.info("Printing Bidder List Report");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportBidderList("/procurement/bidderList/bidderList", "Procurement.bidder list", messages, locale, configuration, suppliers, packageNumber, description, project,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printRequiredRetentions(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printRequiredRetentions");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportRequiredRetentions("/procurement/RequiredRetentionReport/requiredRetentionReport", "Procurement.Required.Retentions", messages, locale, configuration, project, sortMap,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printRequiredSecurity(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printRequiredRetentions");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportRequiredSecurityDeposits("/procurement/RequiredSecurityReport/RequiredSecurityReport", "Procurement.Required.Security.Deposits", messages, locale, configuration, project, sortMap,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }


    public void printSummaryPurchaseOrder(final ProjectEntity project, final Map<String, Boolean> sortMap,Timestamp sortFrom,Timestamp sortTo) {
        log.info("printSummaryPurchaseOrder");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportSummaryPurchaseOrder("/procurement/summaryPurchaseOrderReport/summaryPOReport", "Procurement.Summary.Purchase.order", messages, locale, configuration, project, sortMap,dataSource,sortFrom,sortTo);
        reportView.printDocument(null);
        openReport = true;
    }

    private void initializeParametersToJasperReport() {
        locale = new Locale(Locale.ENGLISH.getLanguage());
        messages = new HashMap<>();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
    public void printSupplierContactInformation(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printSupplierContactInformation");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportSupplierContactInformation("/procurement/supplierContactInformation/SupplierContactInformation", "Procurement.Supplier.contact.information", messages, locale, configuration, project, sortMap,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printDetailedSupplierInformation(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printDetailedSupplierInformation");
        openReport = false;
        initializeParametersToJasperReport();
        if(project.getId().longValue()>0L) {
            ReportView reportView = new ReportDetailedSupplierInformation("/procurement/DetailedSupplierInformation/DetailedSupplierInformation", "Procurement.Detailed.supplier.information", messages, locale, configuration, project, sortMap, dataSource);
            reportView.printDocument(null);
        }else{
            ReportView reportView = new ReportDetailedSupplierInformation("/procurement/DetailedSupplierInformation/DetailedSupplierFullInformation", "Procurement.Detailed.supplier.information", messages, locale, configuration, project, sortMap, dataSource);
            reportView.printDocument(null);
        }
        openReport = true;
    }
    public void printUncommittedData(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printUncommittedData");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportUncommittedData("/procurement/uncommittedDataReport/UncommittedDataReport", "Procurement.uncommitted.data", messages, locale, configuration, project, sortMap,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printMaterialRequisition(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printMaterialRequisition");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportMaterialRequisition("/procurement/MaterialRequisitions/MaterialRequisitions", "Procurement.material.requisition", messages, locale, configuration, project, sortMap,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

}

package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
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
        ReportView reportView = new ReportDeliverables("/procurement/deliverables/reportDeliverables", "Procurement.Deliverables", messages, locale, configuration, po, projectId, termsPoNo,entityManager);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printReportExpediting(final PurchaseOrderEntity po, final Long projectId, final String termsPoNo) {
        log.info("public void printReportExpediting()");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportExpediting("/procurement/expediting/reportExpediting", "Procurement.Deliverables", messages, locale, configuration, po, projectId, termsPoNo,entityManager);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printPurchaseOrder(final PurchaseOrderEntity po,List<ScopeSupplyEntity> list,String preamble,List<ClausesEntity> clausesList) {
        log.info("printPurchaseOrder(purchaseOrderId[" + po.getId() + "])");
        openReport = false;
        initializeParametersToJasperReport();
        List<CashflowEntity> cashflows=cashflowService.findByPoId(po.getPoEntity().getId());
        System.out.println("PO ID "+po.getId());
        System.out.println("cashflow size "+cashflows.size());
        ReportView reportView = new ReportPurchaseOrder("/procurement/printPo/PrintPurchaseOrder", "Procurement.PurchaseOrder", messages, locale, configuration, po, list, preamble,clausesList,cashflows.size()>0?cashflows.get(0):null);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printProjectPurchaseOrder(final ProjectEntity project, final List<PurchaseOrderEntity> poList, final String strSortBy) {
        log.info("printProjectPurchaseOrder");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportProjectProcurement("/procurement/projectProcurementReport/projectProcurementReport", "Procurement.PurchaseOrder", messages, locale, configuration, poList, project, strSortBy);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printDetailedProcurementReport(final ProjectEntity project,final Map<String, Boolean> sortMap) {
        log.info("printDetailedProcurementReport");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportDetailedProcurement("/procurement/detailedProcurementReport/detailedProcurementReport", "Procurement.PurchaseOrder", messages, locale, configuration,  project, sortMap);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printCommittedCurrenciesReport(final ProjectEntity project,final Map<String, Boolean> sortMap) {
        log.info("printDetailedProcurementReport");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportCommittedCurrencies("/procurement/committedCurrenciesReport/committedCurrenciesReport", "Procurement.PurchaseOrder", messages, locale, configuration,  project, sortMap);
        reportView.printDocument(null);
        openReport = true;
    }


    public void printBidderList(List<Long> suppliers, String packageNumber, String description, String comments, ProjectEntity project) {
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportBidderList("/procurement/bidderList/bidderList", "Procurement.bidder list", messages, locale, configuration, suppliers, packageNumber, description, comments,  project);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printRequiredRetentions(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printRequiredRetentions");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportRequiredRetentions("/procurement/RequiredRetentionReport/requiredRetentionReport", "Procurement.PurchaseOrder", messages, locale, configuration, project, sortMap);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printSummaryPurchaseOrder(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printRequiredRetentions");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportSummaryPurchaseOrder("/procurement/summaryPurchaseOrderReport/summaryPOReport", "Procurement.PurchaseOrder", messages, locale, configuration, project, sortMap);
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
        ReportView reportView = new ReportSupplierContactInformation("/procurement/supplierContactInformation/SupplierContactInformation", "Procurement.PurchaseOrder", messages, locale, configuration, project, sortMap);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printDetailedSupplierInformation(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printDetailedSupplierInformation");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportDetailedSupplierInformation("/procurement/DetailedSupplierInformation/DetailedSupplierInformation", "Procurement.PurchaseOrder", messages, locale, configuration, project, sortMap);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printUncommittedData(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printUncommittedData");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportUncommittedData("/procurement/uncommittedDataReport/UncommittedDataReport", "Procurement.PurchaseOrder", messages, locale, configuration, project, sortMap);
        reportView.printDocument(null);
        openReport = true;
    }
    public void printMaterialRequisition(final ProjectEntity project, final Map<String, Boolean> sortMap) {
        log.info("printMaterialRequisition");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportMaterialRequisition("/procurement/MaterialRequisitions/MaterialRequisitions", "Procurement.PurchaseOrder", messages, locale, configuration, project, sortMap);
        reportView.printDocument(null);
        openReport = true;
    }

}

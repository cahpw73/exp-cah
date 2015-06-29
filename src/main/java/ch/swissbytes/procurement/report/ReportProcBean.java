package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Purchase;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;
import ch.swissbytes.procurement.boundary.report.expediting.ExpeditingDto;

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


    private List<PurchaseOrderEntity> selected;


    private String typeId;

    private boolean isAllProviders;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private Configuration configuration;

    public Boolean getOpenReport() {
        return openReport;
    }

    @PostConstruct
    public void init() {
        log.info("ReportBean init!");
        selected = new ArrayList<>();
    }


    @PreDestroy
    public void destroy() {
        log.info("Report Bean destroyed!");
    }

    public void printReportDeliverables(final PurchaseOrderEntity po, final Long projectId, final String termsPoNo) {
        log.info("public void printReportDeliverables()");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportDeliverables("/procurement/deliverables/reportDeliverables", "Procurement.Deliverables", messages, locale, configuration, po, projectId, termsPoNo);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printReportExpediting(final PurchaseOrderEntity po, final Long projectId, final String termsPoNo) {
        log.info("public void printReportExpediting()");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportExpediting("/procurement/expediting/reportExpediting", "Procurement.Deliverables", messages, locale, configuration, po, projectId, termsPoNo);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printPurchaseOrder(final PurchaseOrderEntity po) {
        log.info("printPurchaseOrder(purchaseOrderId[" + po.getId() + "])");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportPurchaseOrder("/procurement/printPo/PrintPurchaseOrder", "Procurement.PurchaseOrder", messages, locale, configuration, po);
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


    public void printBidderList(List<Long> suppliers, String packageNumber, String description, String comments, String codeProject, String project) {
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportBidderList("/procurement/bidderList/bidderList", "Procurement.bidder list", messages, locale, configuration, suppliers, packageNumber, description, comments, codeProject, project);
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

    private void initializeParametersToJasperReport() {
        locale = new Locale(Locale.ENGLISH.getLanguage());
        messages = new HashMap<String, String>();
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

}

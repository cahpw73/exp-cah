package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.ReportPurchaseOrder;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;

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

    public void printReportDeliverables(final List<DeliverableDto> dtos, final PurchaseOrderEntity po,final Long projectId) {
        log.info("public void printReportDeliverables()");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportDeliverables("/procurement/deliverables/reportDeliverables", "Procurement.Deliverables", messages, locale, configuration,dtos,po,projectId);
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

package ch.swissbytes.fqmes.report;


import ch.swissbytes.fqmes.control.purchase.PurchaseOrderService;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import org.omnifaces.util.Messages;

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
public class ReportBean implements Serializable {

    private Logger log = Logger.getLogger(ReportBean.class.getName());

    @PersistenceContext(unitName = "fqmPU")
    private EntityManager entityManager;



    private Locale locale;

    private Map<String,String> messages;

    private Boolean openReport=false;



private List<PurchaseOrderEntity> selected;



    private String typeId;

    private boolean isAllProviders;

    @Inject
    private PurchaseOrderService service;

public Boolean getOpenReport(){
    return openReport;
}

    @PostConstruct
    public void init(){
        log.info("ReportBean init!");
        selected=new ArrayList<>();


    }

    public List<PurchaseOrderEntity> getSelected(){
        return selected;
    }
    public void addPurchaseOrder(final Long id){
        log.info("public void addPurchaseOrder(final Long id="+id+")");
        selected.add(service.load(id));
    }
    public void cleanPurchaseSelected(){
        selected=new ArrayList<>();
    }

    @PreDestroy
    public void destroy(){
        log.info("Report Bean destroyed!");
    }

    public void printReportReceivableManifest(){
        log.info("public void printReportReceivableManifest()");
        openReport=false;
        if(selected!=null&& selected.size()>0){
            initializeParametersToJasperReport();
            ReportView reportView = new ReportPurchaseOrder("/receivableManifest/receivableManifest","Receivable.Manifest", messages,locale, entityManager,selected);
            reportView.printDocument(null);
            openReport=true;
        }else{

            Messages.addGlobalError("you should select at least one purchase order");
        }
    }
    public void printReportJobSummary(){
        log.info("public void printReportJobSummary()");
        openReport=false;
        if(selected!=null&& selected.size()>0){
            initializeParametersToJasperReport();
            ReportView reportView = new ReportPurchaseOrder("/jobSummary/JobSummary","Job.Summary", messages,locale, entityManager,selected);
            reportView.printDocument(null);
            openReport=true;
        }else{

            Messages.addGlobalError("you should select at least one purchase order");
        }
    }






    private void initializeParametersToJasperReport(){
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



    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }



    public boolean isAllProviders() {
        return isAllProviders;
    }

    public void setAllProviders(boolean isAllProviders) {
        this.isAllProviders = isAllProviders;
    }
}

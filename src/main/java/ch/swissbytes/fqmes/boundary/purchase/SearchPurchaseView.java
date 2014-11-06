package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.fqmes.model.Filter;

/**
 * Created by alvaro on 9/25/14.
 */

public class SearchPurchaseView implements Filter {


    private String project;
    private String po;
    private String variation;
    private String supplier;
    private String poTitle;
    private String responsibleExpediting;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }

    public String getResponsibleExpediting() {
        return responsibleExpediting;
    }

    public void setResponsibleExpediting(String responsibleExpediting) {
        this.responsibleExpediting = responsibleExpediting;
    }

    @Override
    public void clean(){
        po=null;
        project=null;
        variation=null;
        supplier=null;
        poTitle=null;
        responsibleExpediting=null;
    }
}

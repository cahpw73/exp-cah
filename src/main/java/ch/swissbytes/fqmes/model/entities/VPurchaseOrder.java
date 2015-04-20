package ch.swissbytes.fqmes.model.entities;

import org.apache.commons.lang.math.NumberUtils;

import javax.inject.Named;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by christian on 23/10/14.
 */
@Named
@Entity
@Table(name = "v_purchase_order")
public class VPurchaseOrder implements Serializable ,Comparable<VPurchaseOrder>{

    private Long id;

    private Long poId;

    private String project;

    private String po;

    private String variation;

    private String poTitle;

    private String supplier;

    private String responsibleExpediting;

    private String incoTerm;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "po_id", nullable = true, insertable=false, updatable=false)
    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    @Column(name="project",updatable = false, insertable = false, nullable = true)
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Column(name="po",updatable = false, insertable = false, nullable = true)
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    @Column(name="variation",updatable = false, insertable = false, nullable = true)
    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    @Column(name="po_title",updatable = false, insertable = false, nullable = true)
    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }

    @Column(name="supplier",updatable = false, insertable = false, nullable = true)
    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Column(name="responsible_expediting",updatable = false, insertable = false, nullable = true)
    public String getResponsibleExpediting() {
        return responsibleExpediting;
    }

    public void setResponsibleExpediting(String responsibleExpediting) {
        this.responsibleExpediting = responsibleExpediting;
    }

    @Column(name="po_inco_term",updatable = false, insertable = false, nullable = true)
    public String getIncoTerm() {
        return incoTerm;
    }

    public void setIncoTerm(String incoTerm) {
        this.incoTerm = incoTerm;
    }

    @Override
    public int compareTo(VPurchaseOrder o) {
        if(o!=null){
            boolean targetAIsNumber=NumberUtils.isNumber(this.getPo()!=null?this.getPo().trim():"");
            boolean targetBIsNumber=NumberUtils.isNumber(o.getPo()!=null?o.getPo().trim():"");
            if(targetAIsNumber&&targetBIsNumber){
                return Double.parseDouble(this.getPo().trim())>Double.parseDouble(o.getPo().trim())?1:-1;
            }else{
                if((!targetAIsNumber&&!targetBIsNumber)){
                    return this.getPo().trim().toLowerCase().compareTo(o.getPo().trim().toLowerCase());
                }else{
                    return targetAIsNumber?-1:1;
                }
            }
        }
        return 0;
    }


}

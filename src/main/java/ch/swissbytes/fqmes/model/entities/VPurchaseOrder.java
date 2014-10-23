package ch.swissbytes.fqmes.model.entities;

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
public class VPurchaseOrder implements Serializable {

    private Long id;

    private Long poId;

    private String project;

    private String po;

    private String variation;

    private String poTitle;

    private String supplier;

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
}

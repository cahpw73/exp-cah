package ch.swissbytes.fqmes.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Named
@Entity
@Table(name = "purchase_order")
public class PurchaseOrderEntity implements Serializable{

    private Long id;
    private String project;
    private String po;
    private String variation;
    private String description;
    private String poTitle;
    private String poIncoTerm;
    private String poIncoTermDescription;
    private Date poDeliveryDate;
    private String poDeliveryDateDescription;
    private String responsibleExpediting;
    private Date introEmail;
    private String introEmailDescription;
    private Date requiredDate;
    private String requiredDateDescription;
    private Date actualDate;
    private String actualDateDescription;
    private Date lastUpdate;
    private StatusEntity status;


    private String poReference;

    private String variationNumber;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PURCHARSE_ORDER_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
/*
    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }*/
    @Column(name="project", nullable=false, length=50)
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
    @Column(name="variation",length=50)
    public String getVariation() {
        return variation;
    }
    @Column(name="po",nullable = false, length=50)
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }
    @Column(name="description", length=1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Column(name= "po_title", length=50)
    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }
    @Column(name="po_inco_term",length=50)
    public String getPoIncoTerm() {
        return poIncoTerm;
    }

    public void setPoIncoTerm(String poIncoTerm) {
        this.poIncoTerm = poIncoTerm;
    }
    @Column(name="po_inco_term_description", length=1000)
    public String getPoIncoTermDescription() {
        return poIncoTermDescription;
    }

    public void setPoIncoTermDescription(String poIncoTermDescription) {
        this.poIncoTermDescription = poIncoTermDescription;
    }


    @Column(name="po_delivery_date")
    public Date getPoDeliveryDate() {
        return poDeliveryDate;
    }

    public void setPoDeliveryDate(Date poDeliveryDate) {
        this.poDeliveryDate = poDeliveryDate;
    }
    @Column(name="po_delivery_date_description",  length=1000)
    public String getPoDeliveryDateDescription() {
        return poDeliveryDateDescription;
    }

    public void setPoDeliveryDateDescription(String poDeliveryDateDescription) {
        this.poDeliveryDateDescription = poDeliveryDateDescription;
    }
    @Column(name="responsible_expediting",  length=100)
    public String getResponsibleExpediting() {
        return responsibleExpediting;
    }

    public void setResponsibleExpediting(String responsibleExpediting) {
        this.responsibleExpediting = responsibleExpediting;
    }

    @Column(name="intro_email_sent", length=255)
    public Date getIntroEmail() {
        return introEmail;
    }

    public void setIntroEmail(Date introEmail) {
        this.introEmail = introEmail;
    }
    @Column(name="intro_email_sent_description", length=1000)
    public String getIntroEmailDescription() {
        return introEmailDescription;
    }

    public void setIntroEmailDescription(String introEmailDescription) {
        this.introEmailDescription = introEmailDescription;
    }

    @Column(name="required_date", length=255)
    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }
    @Column(name="required_date_description", length=1000)
    public String getRequiredDateDescription() {
        return requiredDateDescription;
    }

    public void setRequiredDateDescription(String requiredDateDescription) {
        this.requiredDateDescription = requiredDateDescription;
    }

    @Column(name="actual_date", length=255)
    public Date getActualDate() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate = actualDate;
    }
    @Column(name="actual_date_description", length=1000)
    public String getActualDateDescription() {
        return actualDateDescription;
    }

    public void setActualDateDescription(String actualDateDescription) {
        this.actualDateDescription = actualDateDescription;
    }
    @Column(name="LAST_UPDATE", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="status_id", nullable=false)
    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    @Transient
    public String getPoReference(){
        poReference=po!=null?po:"";
        return poReference;
    }
    @Transient
    public String getVariationNumber(){
        variationNumber=variation!=null?variation:"";
        return variationNumber;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseOrderEntity)) return false;
        PurchaseOrderEntity that = (PurchaseOrderEntity) o;
        return that.hashCode()==this.hashCode();
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (po != null ? po.hashCode() : 0);
        result = 31 * result + (variation != null ? variation.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (poTitle != null ? poTitle.hashCode() : 0);
        result = 31 * result + (poIncoTerm != null ? poIncoTerm.hashCode() : 0);
        result = 31 * result + (poIncoTermDescription != null ? poIncoTermDescription.hashCode() : 0);
        result = 31 * result + (poDeliveryDate != null ? poDeliveryDate.hashCode() : 0);
        result = 31 * result + (poDeliveryDateDescription != null ? poDeliveryDateDescription.hashCode() : 0);
        result = 31 * result + (responsibleExpediting != null ? responsibleExpediting.hashCode() : 0);
        result = 31 * result + (introEmail != null ? introEmail.hashCode() : 0);
        result = 31 * result + (introEmailDescription != null ? introEmailDescription.hashCode() : 0);
        result = 31 * result + (requiredDate != null ? requiredDate.hashCode() : 0);
        result = 31 * result + (requiredDateDescription != null ? requiredDateDescription.hashCode() : 0);
        result = 31 * result + (actualDate != null ? actualDate.hashCode() : 0);
        result = 31 * result + (actualDateDescription != null ? actualDateDescription.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (status != null ? status.getId().hashCode() : 0);
        return result;
    }
}

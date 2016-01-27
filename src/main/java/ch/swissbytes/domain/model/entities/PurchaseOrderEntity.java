package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import org.apache.commons.lang.StringUtils;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private String projectNameComment;
    private String poTitle;
    private String incoTerm;
    private String fullIncoTerms;
    private Date poDeliveryDate;
    private Date poExpeditingDeliveryDate;
    private String deliveryDateComment;
    private String responsibleExpediting;
    private Date introEmail;
    private String introEmailSentComment;
    private Date requiredDate;
    private String requiredSiteDateComment;
    private Date actualDate;
    private String actualSiteDateComment;
    private Date lastUpdate;
    private StatusEntity status;
    private String rfeComment;
    private ExpeditingStatusEnum purchaseOrderStatus;
    private ProjectEntity projectEntity;
    private PurchaseOrderProcurementEntity purchaseOrderProcurementEntity;
    private Integer orderedVariation;
    private Boolean locked;
    private UserEntity lockedBy;
    private Date lastActivityUpdate;
    private String expeditingTitle;
    private Date nextKeyDate;
    private String nextKeyDateComment;


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

    @Size(max = 50)
    @Column(name="project", nullable=false, length=50)
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Size(max = 50)
    @Column(name="variation",length=50)
    public String getVariation() {
        return variation;
    }

    @Size(max = 50)
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

    @Size(max = 1000)
    @Column(name="PROJECT_NAME_COMMENT", length=1000)
    public String getProjectNameComment() {
        return projectNameComment;
    }

    public void setProjectNameComment(String projectNameComment) {
        this.projectNameComment = projectNameComment;
    }
    @Size(max = 50)
    @Column(name= "po_title", length=50)
    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }
    @Size(max = 50)
    @Column(name="po_inco_term",length=50)
    public String getIncoTerm() {
        return incoTerm;
    }

    public void setIncoTerm(String incoTerm) {
        this.incoTerm = incoTerm;
    }

    @Size(max = 1000)
    @Column(name="FULL_INCO_TERM", length=1000)
    public String getFullIncoTerms() {
        return fullIncoTerms;
    }

    public void setFullIncoTerms(String fullIncoTerms) {
        this.fullIncoTerms = fullIncoTerms;
    }


    @Column(name="po_delivery_date")
    public Date getPoDeliveryDate() {
        return poDeliveryDate;
    }

    public void setPoDeliveryDate(Date poDeliveryDate) {
        this.poDeliveryDate = poDeliveryDate;
    }

    @Column(name="po_expediting_delivery_date")
    public Date getPoExpeditingDeliveryDate() {
        return poExpeditingDeliveryDate;
    }

    public void setPoExpeditingDeliveryDate(Date poExpeditingDeliveryDate) {
        this.poExpeditingDeliveryDate = poExpeditingDeliveryDate;
    }

    @Size(max = 1000)
    @Column(name="DELIVERY_DATE_COMMENT",  length=1000)
    public String getDeliveryDateComment() {
        return deliveryDateComment;
    }

    public void setDeliveryDateComment(String deliveryDateComment) {
        this.deliveryDateComment = deliveryDateComment;
    }

    @Size(max = 100)
    @Column(name="responsible_expediting",  length=100)
    public String getResponsibleExpediting() {
        return responsibleExpediting;
    }

    public void setResponsibleExpediting(String responsibleExpediting) {
        this.responsibleExpediting = responsibleExpediting;
    }


    @Column(name="intro_email_sent")
    public Date getIntroEmail() {
        return introEmail;
    }

    public void setIntroEmail(Date introEmail) {
        this.introEmail = introEmail;
    }

    @Size(max = 1000)
    @Column(name="INTRO_EMAIL_SENT_COMMENT", length=1000)
    public String getIntroEmailSentComment() {
        return introEmailSentComment;
    }

    public void setIntroEmailSentComment(String introEmailSentComment) {
        this.introEmailSentComment = introEmailSentComment;
    }

    @Column(name="required_date")
    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }

    @Size(max = 1000)
    @Column(name="REQUIRED_SITE_DATE_COMMENT", length=1000)
    public String getRequiredSiteDateComment() {
        return requiredSiteDateComment;
    }

    public void setRequiredSiteDateComment(String requiredSiteDateComment) {
        this.requiredSiteDateComment = requiredSiteDateComment;
    }

    @Column(name="actual_date")
    public Date getActualDate() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate = actualDate;
    }

    @Size(max = 1000)
    @Column(name="ACTUAL_SITE_DATE_COMMENT", length=1000)
    public String getActualSiteDateComment() {
        return actualSiteDateComment;
    }

    public void setActualSiteDateComment(String actualSiteDateComment) {
        this.actualSiteDateComment = actualSiteDateComment;
    }

    @Column(name="EXPEDITING_TITLE",  nullable = false)
    public String getExpeditingTitle() {
        return expeditingTitle;
    }

    public void setExpeditingTitle(String expeditingTitle) {
        this.expeditingTitle = expeditingTitle;
    }

    @Column(name="NEXT_KEY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNextKeyDate() {
        return nextKeyDate;
    }

    public void setNextKeyDate(Date nextKeyDate) {
        this.nextKeyDate = nextKeyDate;
    }

    @Size(max = 1000)
    @Column(name="NEXT_KEY_DATE_COMMENT",  length=1000)
    public String getNextKeyDateComment() {
        return nextKeyDateComment;
    }

    public void setNextKeyDateComment(String nextKeyDateComment) {
        this.nextKeyDateComment = nextKeyDateComment;
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

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name="PURCHASE_ORDER_STATUS",nullable = false)
    public ExpeditingStatusEnum getPurchaseOrderStatus() {
        return purchaseOrderStatus;
    }

    public void setPurchaseOrderStatus(ExpeditingStatusEnum purchaseOrderStatus) {
        this.purchaseOrderStatus = purchaseOrderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseOrderEntity)) return false;
        PurchaseOrderEntity that = (PurchaseOrderEntity) o;
        return that.hashCode()==this.hashCode();
    }
    @Size(max = 1000)
    @Column(name="RFE_COMMENT")
    public String getRfeComment() {
        return rfeComment;
    }

    public void setRfeComment(String rfeComment) {
        this.rfeComment = rfeComment;
    }


    public void setProjectEntity(ProjectEntity project) {
        this.projectEntity = project;
    }
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_id", nullable=false)
    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public PurchaseOrderProcurementEntity getPurchaseOrderProcurementEntity() {
        return purchaseOrderProcurementEntity;
    }

    public void setPurchaseOrderProcurementEntity(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity) {
        this.purchaseOrderProcurementEntity = purchaseOrderProcurementEntity;
    }

    public Integer getOrderedVariation() {
        return orderedVariation;
    }

    public void setOrderedVariation(Integer orderedVariation) {
        this.orderedVariation = orderedVariation;
    }

    @Transient
    public String commentForProject(){
        String commentForProject="";
        if(StringUtils.isNotEmpty(projectNameComment)){
            commentForProject=projectNameComment;
        }else{
            commentForProject=(projectEntity!=null&&StringUtils.isNotEmpty(projectEntity.getTitle())?projectEntity.getTitle():"");
        }
        return commentForProject;
    }

   @Column(name="locked")
    public Boolean isLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="locked_by")
    public UserEntity getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(UserEntity lockedBy) {
        this.lockedBy = lockedBy;
    }

    @Column(name="LAST_ACTIVITY_UPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastActivityUpdate() {
        return lastActivityUpdate;
    }

    public void setLastActivityUpdate(Date lastActivityUpdate) {
        this.lastActivityUpdate = lastActivityUpdate;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (po != null ? po.hashCode() : 0);
        result = 31 * result + (variation != null ? variation.hashCode() : 0);
        result = 31 * result + (projectNameComment != null ? projectNameComment.hashCode() : 0);
        result = 31 * result + (poTitle != null ? poTitle.hashCode() : 0);
        result = 31 * result + (incoTerm != null ? incoTerm.hashCode() : 0);
        result = 31 * result + (fullIncoTerms != null ? fullIncoTerms.hashCode() : 0);
        result = 31 * result + (poDeliveryDate != null ? poDeliveryDate.hashCode() : 0);
        result = 31 * result + (deliveryDateComment != null ? deliveryDateComment.hashCode() : 0);
        result = 31 * result + (responsibleExpediting != null ? responsibleExpediting.hashCode() : 0);
        result = 31 * result + (introEmail != null ? introEmail.hashCode() : 0);
        result = 31 * result + (introEmailSentComment != null ? introEmailSentComment.hashCode() : 0);
        result = 31 * result + (requiredDate != null ? requiredDate.hashCode() : 0);
        result = 31 * result + (requiredSiteDateComment != null ? requiredSiteDateComment.hashCode() : 0);
        result = 31 * result + (actualDate != null ? actualDate.hashCode() : 0);
        result = 31 * result + (actualSiteDateComment != null ? actualSiteDateComment.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (status != null ? status.getId().hashCode() : 0);
        result = 31 * result + (rfeComment != null ? rfeComment.hashCode() : 0);
        return result;
    }
}

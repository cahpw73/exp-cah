package ch.swissbytes.domain.model.entities;

/**
 * Created by christian on 27/01/16.
 */


import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "po_document")
public class PODocumentEntity extends RecordEditable<PODocumentEntity> implements Serializable{

    private Long id;
    private String description;
    private String code;
    private StatusEnum status;
    private Date lastUpdate;
    private Integer ordered;
    private String numberPODoc;
    private PurchaseOrderProcurementEntity poProcurementEntity;
    private ProjectDocumentEntity projectDocumentEntity;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PO_DOCUMENT_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }



    @Column (name = "status",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Column(name = "last_update",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Lob
    @Size(max = 20000)
    @Column(name = "description", nullable = false, length = 20000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Size(max = 250)
    @Column(name = "code", nullable = false, length = 250)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "ordered")
    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    @Column(name = "number_po_doc")
    public String getNumberPODoc() {
        return numberPODoc;
    }

    public void setNumberPODoc(String numberPODoc) {
        this.numberPODoc = numberPODoc;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public PurchaseOrderProcurementEntity getPoProcurementEntity() {
        return poProcurementEntity;
    }

    public void setPoProcurementEntity(PurchaseOrderProcurementEntity po) {
        this.poProcurementEntity = po;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_document_id")
    public ProjectDocumentEntity getProjectDocumentEntity() {
        return projectDocumentEntity;
    }

    public void setProjectDocumentEntity(ProjectDocumentEntity projectDocumentEntity) {
        this.projectDocumentEntity = projectDocumentEntity  ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PODocumentEntity that = (PODocumentEntity) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (!id.equals(that.id)) return false;
        if (lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null) return false;
        if (ordered != null ? !ordered.equals(that.ordered) : that.ordered != null) return false;
        if (poProcurementEntity != null ? !poProcurementEntity.equals(that.poProcurementEntity) : that.poProcurementEntity != null)
            return false;
        if (projectDocumentEntity != null ? !projectDocumentEntity.equals(that.projectDocumentEntity) : that.projectDocumentEntity != null)
            return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (ordered != null ? ordered.hashCode() : 0);
        result = 31 * result + (poProcurementEntity != null ? poProcurementEntity.hashCode() : 0);
        result = 31 * result + (projectDocumentEntity != null ? projectDocumentEntity.hashCode() : 0);
        return result;
    }
}

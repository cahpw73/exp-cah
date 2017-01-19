package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "project_text_snippet")
public class ProjectTextSnippetEntity extends RecordEditable<ProjectTextSnippetEntity> implements Serializable{

    private Long id;
    private String code;
    private String description;
    private StatusEnum status;
    private Date lastUpdate;
    private ProjectEntity project;
    private TextSnippetEntity textSnippet;
    private PurchaseOrderEntity purchaseOrder;
    private String descriptionSnippet;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PROJECT_TEXT_SNIPPET_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Size(max = 250)
    @Column(name = "code", nullable = false, length = 250)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    //@Lob
    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Size(max = 100000)
    @Column(name = "description_snippet", nullable = true, length = 100000)
    public String getDescriptionSnippet() {
        return descriptionSnippet;
    }

    public void setDescriptionSnippet(String descriptionSnippet) {
        this.descriptionSnippet = descriptionSnippet;
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

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_id", nullable = false)
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="text_snippet_id", nullable = true)
    public TextSnippetEntity getTextSnippet() {
        return textSnippet;
    }

    public void setTextSnippet(TextSnippetEntity textSnippet) {
        this.textSnippet = textSnippet;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectTextSnippetEntity that = (ProjectTextSnippetEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

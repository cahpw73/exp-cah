package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "clauses_text")
public class ClausesEntity extends RecordEditable<ClausesEntity> implements Serializable{

    private Long id;
    private String code;
    private String clauses;
    private StatusEnum status;
    private Date lastUpdate;
    private Integer ordered;
    private String numberClause;
    private TextEntity text;
    private ProjectTextSnippetEntity projectTextSnippet;
    //private String descriptionSnippet;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "CLAUSES_TEXT_ID_SEQ", allocationSize = 1)
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


    @Lob
    @Column(name = "clauses",nullable = true)
    public String getClauses() {
        return clauses;
    }

    public void setClauses(String clauses) {
        this.clauses = clauses;
    }

    /*@Size(max = 100000)
    @Column(name = "description_snippet", nullable = true, length = 100000)
    public String getDescriptionSnippet() {
        return descriptionSnippet;
    }

    public void setDescriptionSnippet(String descriptionSnippet) {
        this.descriptionSnippet = descriptionSnippet;
    }*/

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

    @Column(name = "ordered")
    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    @Column(name = "number_clause")
    public String getNumberClause() {
        return numberClause;
    }

    public void setNumberClause(String numberClause) {
        this.numberClause = numberClause;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="text_id")
    public TextEntity getText() {
        return text;
    }


    public void setText(TextEntity text) {
        this.text = text;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_text_snippet_id")
    public ProjectTextSnippetEntity getProjectTextSnippet() {
        return projectTextSnippet;
    }

    public void setProjectTextSnippet(ProjectTextSnippetEntity projectTextSnippet) {
        this.projectTextSnippet = projectTextSnippet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClausesEntity entity = (ClausesEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

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
@Table(name = "text_snippet")
public class TextSnippetEntity extends RecordEditable<TextSnippetEntity> implements Serializable{

    private Long id;
    private String textSnippet;
    private String code;
    private StatusEnum status;
    private Date lastUpdate;
    private ProjectEntity project;
    private String descriptionSnippet;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "STANDARD_TEXT_ID_SEQ", allocationSize = 1)
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


    //@Lob
    @Column(name = "TEXT_SNIPPET", nullable = true)
    public String getTextSnippet() {
        return textSnippet;
    }

    public void setTextSnippet(String textSnippet) {
        this.textSnippet = textSnippet;
    }

    @Size(max = 100000)
    @Column(name = "description_snippet", nullable = true, length = 100000)
    public String getDescriptionSnippet() {
        return descriptionSnippet;
    }

    public void setDescriptionSnippet(String descriptionSnippet) {
        this.descriptionSnippet = descriptionSnippet;
    }

    @Size(max = 250)
    @Column(name = "code", nullable = false, length = 250)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_id")
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextSnippetEntity that = (TextSnippetEntity) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        /*if (descriptionSnippet != null ? !descriptionSnippet.equals(that.descriptionSnippet) : that.descriptionSnippet != null)
            return false;*/
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null) return false;
        if (project != null ? !project.equals(that.project) : that.project != null) return false;
        if (status != that.status) return false;
        if (textSnippet != null ? !textSnippet.equals(that.textSnippet) : that.textSnippet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (textSnippet != null ? textSnippet.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        //result = 31 * result + (descriptionSnippet != null ? descriptionSnippet.hashCode() : 0);
        return result;
    }
}

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
    private String clauses;
    private TextEntity text;
    private TextSnippetEntity textSnippet;

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

    @Size(max = 1000)
    @Column(name = "clauses",length = 1000)
    public String getClauses() {
        return clauses;
    }

    public void setClauses(String clauses) {
        this.clauses = clauses;
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
    @JoinColumn(name="text_snippet_id")
    public TextSnippetEntity getTextSnippet() {
        return textSnippet;
    }


    public void setTextSnippet(TextSnippetEntity textSnippet) {
        this.textSnippet = textSnippet;
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

package ch.swissbytes.domain.model.entities;

import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by alvaro on 6/3/2015.
 */

@Entity
@Table(name = "deliverable")
public class DeliverableEntity extends RecordEditable<DeliverableEntity> implements Serializable {

    private Long id;

    private String description;
    private Integer quantity;
    private Integer noDays;
    private Date requiredDate;
    private StatusEnum status;
    private Date lastUpdate;
    private POEntity poEntity;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "REQUISITION_ID_SEQ", allocationSize = 1)
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





    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "required_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id")
    public POEntity getPoEntity() {
        return poEntity;
    }

    public void setPoEntity(POEntity poEntity) {
        this.poEntity = poEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeliverableEntity entity = (DeliverableEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }
    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    @Column(name = "no_days")
    public Integer getNoDays() {
        return noDays;
    }

    public void setNoDays(Integer noDays) {
        this.noDays = noDays;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}

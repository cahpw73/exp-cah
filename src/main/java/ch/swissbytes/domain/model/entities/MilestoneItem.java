package ch.swissbytes.domain.model.entities;

import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by alvaro on 9/11/2015.
 */
@Entity
@Table(name = "milestone_item")
public class MilestoneItem {

    private Long id;
    private StatusEnum statusEnum;
    private Date lastUpdate;
    private String item;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "MILESTONE_ITEM_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    @Column(name = "last_update", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    @Size(max = 1000)
    @Column(name = "item", length = 1000,nullable = false)
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}

package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Named
@Entity
@Table(name = "supplier_category")
public class SupplierBrand implements Serializable{

    private Long id;
    private StatusEnum status;
    private SupplierProcEntity supplier;
    private BrandEntity brand;
    private Date lastUpdate;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "SUPPLIER_CATEGORY_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="LAST_UPDATE", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column (name = "status",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="supplier_id")
    public SupplierProcEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierProcEntity supplier) {
        this.supplier = supplier;
    }
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="brand_id")
    public BrandEntity getBrand() {
        return brand;
    }

    public void setBrand(BrandEntity category) {
        this.brand = category;
    }
}

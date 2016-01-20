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
@Table(name = "expediting_status")
public class ExpeditingStatusEntity implements Serializable{

    private Long id;
    private ExpeditingStatusEnum purchaseOrderStatus;
    private PurchaseOrderEntity purchaseOrderEntity;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "EXPEDITING_STATUS_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="po_id", nullable = false)
    public PurchaseOrderEntity getPurchaseOrderEntity() {
        return purchaseOrderEntity;
    }

    public void setPurchaseOrderEntity(PurchaseOrderEntity purchaseOrderEntity) {
        this.purchaseOrderEntity = purchaseOrderEntity;
    }
}

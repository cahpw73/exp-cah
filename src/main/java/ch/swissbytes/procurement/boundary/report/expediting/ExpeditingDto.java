package ch.swissbytes.procurement.boundary.report.expediting;

import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.types.POStatusEnum;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Christian-Alba on 14/06/15.
 */
public class ExpeditingDto implements Serializable {

    private Long itemId;
    private String poNo;
    private String varNo;
    private String item;
    private String description;
    private Boolean excludeFromExpediting;
    private POStatusEnum poStatusEnum;

    public ExpeditingDto() {

    }

    public ExpeditingDto(PurchaseOrderEntity p,ScopeSupplyEntity s) {
        this.itemId = s.getId();
        this.poNo = p.getPo();
        this.varNo = p.getVariation();
        this.item = s.getCode();
        this.description = s.getDescription();
        this.excludeFromExpediting = s.getExcludeFromExpediting();
        this.poStatusEnum = p.getPoEntity().getPoProcStatus();
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getVarNo() {
        return varNo;
    }

    public void setVarNo(String varNo) {
        this.varNo = varNo;
    }

    public Boolean getExcludeFromExpediting() {
        return excludeFromExpediting;
    }

    public void setExcludeFromExpediting(Boolean excludeFromExpediting) {
        this.excludeFromExpediting = excludeFromExpediting;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public POStatusEnum getPoStatusEnum() {
        return poStatusEnum;
    }

    public void setPoStatusEnum(POStatusEnum poStatusEnum) {
        this.poStatusEnum = poStatusEnum;
    }
}

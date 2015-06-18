package ch.swissbytes.procurement.boundary.report.deliverable;

import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Christian-Alba on 14/06/15.
 */
public class DeliverableDto implements Serializable {

    private String poNo;
    private String varNo;
    private String poDescription;
    private String delNo;
    private String description;
    private String qty;
    private Date requiredDate;
    private Date forecastDate;
    private Date receivedDate;

    public DeliverableDto() {

    }

    public DeliverableDto(PurchaseOrderEntity p, DeliverableEntity d) {
        this.poNo = p.getPoEntity().getOrderNumber();
        this.varNo = p.getPoEntity().getVarNumber();
        this.poDescription = "";
        this.delNo = d.getNoDays() != null ? d.getNoDays().toString() : "";
        this.description = StringUtils.isNotEmpty(d.getDescription()) ? d.getDescription() : "";
        this.qty = d.getQuantity() != null ? d.getQuantity().toString() : "";
        this.requiredDate = d.getRequiredDate() != null ? d.getRequiredDate() : null;
        this.forecastDate = null;
        this.receivedDate = null;
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

    public String getPoDescription() {
        return poDescription;
    }

    public void setPoDescription(String poDescription) {
        this.poDescription = poDescription;
    }

    public String getDelNo() {
        return delNo;
    }

    public void setDelNo(String delNo) {
        this.delNo = delNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }

    public Date getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(Date forecastDate) {
        this.forecastDate = forecastDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
}

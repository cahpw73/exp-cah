package ch.swissbytes.procurement.boundary.report.deliverable;

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

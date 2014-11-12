package ch.swissbytes.fqmes.model.entities;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by Alvaro on 8/10/14.
 */
@Named
@Entity
@Table(name = "TRANSIT_DELIVERY_POINT")
public class TransitDeliveryPointEntity implements EntityTbl{

    private Long id;
    private StatusEntity status;
    private String location;
    private Integer leadTime;
    private TimeMeasurementEnum measurementTime;
    private Date forecastDeliveryDate;
    private Date actualDeliveryDate;
    private ScopeSupplyEntity scopeSupply;
    private String comment;
    private Date lastUpdate;
    private Boolean isForecastSiteDateManual;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "TRANSIT_DELIVERY_POINT_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="LEAD_TIME")
    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name="MEASUREMENT_TIME")
    public TimeMeasurementEnum getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(TimeMeasurementEnum measurementTime) {
        this.measurementTime = measurementTime;
    }
    @Column(name="FORECAST_DELIVERY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getForecastDeliveryDate() {
        return forecastDeliveryDate;
    }

    public void setForecastDeliveryDate(Date forecastDeliveryDate) {
        this.forecastDeliveryDate = forecastDeliveryDate;
    }
    @Column(name="ACTUAL_DELIVERY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="SCOPE_SUPPLY_ID", nullable=false)
    public ScopeSupplyEntity getScopeSupply() {
        return scopeSupply;
    }

    public void setScopeSupply(ScopeSupplyEntity scopeSupply) {
        this.scopeSupply = scopeSupply;
    }

    @Size(max = 50)
    @Column(name="location", nullable = false,length=50)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    @Size(max = 1000)
    @Column(name="comments", length=1000)
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="status_id", nullable=false)
    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }


    @Column(name="LAST_UPDATE", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransitDeliveryPointEntity)) return false;
        TransitDeliveryPointEntity that = (TransitDeliveryPointEntity) o;
        return that.hashCode()==this.hashCode();
    }

    @NotNull
    @Column(name="IS_FORECAST_SITE_DATE_MANUAL",nullable = false)
    public Boolean getIsForecastSiteDateManual() {
        return isForecastSiteDateManual;
    }

    public void setIsForecastSiteDateManual(Boolean isForecastSiteDateManual) {
        this.isForecastSiteDateManual = isForecastSiteDateManual;
    }

    @Transient
    public String getCustomLeadTime(){
        return Integer.toString(leadTime!=null?leadTime.intValue():0)+"-"+Integer.toString(measurementTime.ordinal());

    }
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (status != null ? status.getId().hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (leadTime != null ? leadTime.hashCode() : 0);
        result = 31 * result + (measurementTime != null ? measurementTime.hashCode() : 0);
        result = 31 * result + (forecastDeliveryDate != null ? forecastDeliveryDate.hashCode() : 0);
        result = 31 * result + (actualDeliveryDate != null ? actualDeliveryDate.hashCode() : 0);
        result = 31 * result + (scopeSupply != null ? scopeSupply.getId().hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (isForecastSiteDateManual != null ? isForecastSiteDateManual.hashCode() : 0);
        return result;
    }
}

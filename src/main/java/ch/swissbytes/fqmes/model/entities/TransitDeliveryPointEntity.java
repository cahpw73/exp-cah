package ch.swissbytes.fqmes.model.entities;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;

import javax.inject.Named;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by Alvaro on 8/10/14.
 */
@Named
@Entity
@Table(name = "TRANSIT_DELIVERY_POINT")
public class TransitDeliveryPointEntity {

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
    @Column(name="location", nullable = false,length=50)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(name="comments", length=500)
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



}

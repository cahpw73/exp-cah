package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "project_currency")
public class ProjectCurrencyEntity implements Serializable{

    private Long id;
    private BigDecimal exchangeRate;
    private BigDecimal currencyFactor;
    private String format;
    private Boolean projectDefault;
    private StatusEnum status;
    private Date lastUpdate;
    private CurrencyEntity currency;
    private ProjectEntity project;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PROJECT_CURRENCY_ID_SEQ", allocationSize = 1)
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

    @Column(name="exchange_rate", nullable=false, precision=18, scale=5)
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Column(name="currency_factor", nullable=false, precision=18, scale=5)
    public BigDecimal getCurrencyFactor() {
        return currencyFactor;
    }

    public void setCurrencyFactor(BigDecimal currencyFactor) {
        this.currencyFactor = currencyFactor;
    }

    @Column(name="format", nullable=true, length = 255)
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Column(name="project_default", nullable=true)
    public Boolean getProjectDefault() {
        return projectDefault;
    }

    public void setProjectDefault(Boolean projectDefault) {
        this.projectDefault = projectDefault;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="currency_id", nullable = false)
    public CurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_id", nullable = false)
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectCurrencyEntity that = (ProjectCurrencyEntity) o;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (exchangeRate != null ? exchangeRate.hashCode() : 0);
        result = 31 * result + (currencyFactor != null ? currencyFactor.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (projectDefault != null ? projectDefault.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        return result;
    }
}

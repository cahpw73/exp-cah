package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "project")
public class ProjectEntity implements Serializable{

    private Long id;
    private String projectNumber;
    private String title;
    private LogoEntity reportLogo;
    private LogoEntity clientLogo;
    private LogoEntity clientFooter;
    private LogoEntity defaultLogo;
    private LogoEntity defaultFooter;
    private String deliveryInstructions;
    private SupplierProcEntity supplierProcurement;
    private StatusEnum status;
    private Date lastUpdate;

    private List<ProjectCurrencyEntity> currencies=new ArrayList<>();

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PROJECT_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Size(max = 255)
    @Column(name = "project_number", nullable = false, length = 250)
    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    @Size(max = 255)
    @Column(name = "title", nullable = true, length = 250)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="report_logo_id", nullable = true)
    public LogoEntity getReportLogo() {
        return reportLogo;
    }

    public void setReportLogo(LogoEntity reportLogo) {
        this.reportLogo = reportLogo;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="client_logo_id", nullable = true)
    public LogoEntity getClientLogo() {
        return clientLogo;
    }

    public void setClientLogo(LogoEntity clientLogo) {
        this.clientLogo = clientLogo;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="client_footer_id", nullable = true)
    public LogoEntity getClientFooter() {
        return clientFooter;
    }

    public void setClientFooter(LogoEntity clientFooter) {
        this.clientFooter = clientFooter;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="default_logo_id", nullable = true)
    public LogoEntity getDefaultLogo() {
        return defaultLogo;
    }

    public void setDefaultLogo(LogoEntity defaultLogo) {
        this.defaultLogo = defaultLogo;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="default_footer_id", nullable = true)
    public LogoEntity getDefaultFooter() {
        return defaultFooter;
    }

    public void setDefaultFooter(LogoEntity defaultFooter) {
        this.defaultFooter = defaultFooter;
    }

    @Size(max = 1000)
    @Column(name = "delivery_instructions", nullable = true, length = 1000)
    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="supplier_procurement_id", nullable = true)
    public SupplierProcEntity getSupplierProcurement() {
        return supplierProcurement;
    }

    public void setSupplierProcurement(SupplierProcEntity supplier) {
        this.supplierProcurement = supplier;
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

    @Transient
    public List<ProjectCurrencyEntity> getCurrencies() {
        return currencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectEntity that = (ProjectEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

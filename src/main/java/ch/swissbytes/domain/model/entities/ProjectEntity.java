package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

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
    private SupplierEntity supplier;
    private StatusEnum status;
    private Date lastUpdate;

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
    @Column(name = "title", nullable = false, length = 250)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="report_logo_id")
    public LogoEntity getReportLogo() {
        return reportLogo;
    }

    public void setReportLogo(LogoEntity reportLogo) {
        this.reportLogo = reportLogo;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="client_logo_id")
    public LogoEntity getClientLogo() {
        return clientLogo;
    }

    public void setClientLogo(LogoEntity clientLogo) {
        this.clientLogo = clientLogo;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="client_footer_id")
    public LogoEntity getClientFooter() {
        return clientFooter;
    }

    public void setClientFooter(LogoEntity clientFooter) {
        this.clientFooter = clientFooter;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="default_logo_id")
    public LogoEntity getDefaultLogo() {
        return defaultLogo;
    }

    public void setDefaultLogo(LogoEntity defaultLogo) {
        this.defaultLogo = defaultLogo;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="default_footer_id")
    public LogoEntity getDefaultFooter() {
        return defaultFooter;
    }

    public void setDefaultFooter(LogoEntity defaultFooter) {
        this.defaultFooter = defaultFooter;
    }

    @Size(max = 255)
    @Column(name = "delivery_instructions", nullable = false, length = 250)
    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="supplier_id")
    public SupplierEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierEntity supplier) {
        this.supplier = supplier;
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


}

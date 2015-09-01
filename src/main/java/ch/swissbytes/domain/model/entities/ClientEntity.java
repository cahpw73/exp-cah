package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 29/6/15.
 */


import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "client")
public class ClientEntity implements Serializable {

    private Long id;
    private String title;
    private String name;
    private LogoEntity reportLogo;
    private LogoEntity clientLogoLeft;
    private LogoEntity clientLogo;
    private LogoEntity clientFooter;
    private LogoEntity defaultLogo;
    private LogoEntity defaultFooter;
    private StatusEnum status;
    private String invoiceTo;
    private Date lastUpdate;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "CLIENT_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Size(max = 250)
    @Column(name = "name", length = 250)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 1000)
    @Column(name = "title", nullable = true, length = 1000)
    public String getTitle() {
        return title;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    @Size(max = 1000)
    @Column(name = "invoice_to", nullable = true, length = 1000)
    public String getInvoiceTo() {
        return invoiceTo;
    }

    public void setInvoiceTo(String invoiceTo) {
        this.invoiceTo = invoiceTo;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_logo_id", nullable = true)
    public LogoEntity getReportLogo() {
        return reportLogo;
    }

    public void setReportLogo(LogoEntity reportLogo) {
        this.reportLogo = reportLogo;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_logo_left_id", nullable = true)
    public LogoEntity getClientLogoLeft() {
        return clientLogoLeft;
    }

    public void setClientLogoLeft(LogoEntity clientLogoLeft) {
        this.clientLogoLeft = clientLogoLeft;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_logo_id", nullable = true)
    public LogoEntity getClientLogo() {
        return clientLogo;
    }

    public void setClientLogo(LogoEntity clientLogo) {
        this.clientLogo = clientLogo;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_footer_id", nullable = true)
    public LogoEntity getClientFooter() {
        return clientFooter;
    }

    public void setClientFooter(LogoEntity clientFooter) {
        this.clientFooter = clientFooter;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_logo_id", nullable = true)
    public LogoEntity getDefaultLogo() {
        return defaultLogo;
    }

    public void setDefaultLogo(LogoEntity defaultLogo) {
        this.defaultLogo = defaultLogo;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_footer_id", nullable = true)
    public LogoEntity getDefaultFooter() {
        return defaultFooter;
    }

    public void setDefaultFooter(LogoEntity defaultFooter) {
        this.defaultFooter = defaultFooter;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Column(name = "last_update", nullable = false)
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
        if (o == null || getClass() != o.getClass()) return false;

        ClientEntity that = (ClientEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

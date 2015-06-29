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
    private LogoEntity reportLogo;
    private LogoEntity clientLogo;
    private StatusEnum status;
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


    @Size(max = 255)
    @Column(name = "title", nullable = true, length = 250)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    @JoinColumn(name = "client_logo_id", nullable = true)
    public LogoEntity getClientLogo() {
        return clientLogo;
    }

    public void setClientLogo(LogoEntity clientLogo) {
        this.clientLogo = clientLogo;
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

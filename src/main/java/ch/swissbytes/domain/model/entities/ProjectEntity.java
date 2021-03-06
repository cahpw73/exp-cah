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
public class ProjectEntity implements Serializable {

    private Long id;
    private String projectNumber;
    private String title;
    private LogoEntity reportLogo;
    private LogoEntity clientLogo;
    private LogoEntity clientFooter;
    private LogoEntity defaultLogo;
    private LogoEntity defaultFooter;
    private String deliveryInstructions;
    private ClientEntity client;
    private StatusEnum status;
    private Date lastUpdate;
    private String invoiceTo;
    private String folderName;

    private List<ProjectCurrencyEntity> currencies = new ArrayList<>();
    private List<ProjectTextSnippetEntity> projectTextSnippetList = new ArrayList<>();
    private List<TextSnippetEntity> globalStandardTextList = new ArrayList<>();
    private List<ProjectDocumentEntity> projectDocumentList = new ArrayList<>();
    private List<MainDocumentEntity> mainDocumentList = new ArrayList<>();


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

    @Column(name = "folder_name")
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
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

    @Size(max = 950, message = "It must contain 950 characters at most")
    @Column(name = "delivery_instructions", nullable = true, length = 1000)
    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
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

    @Transient
    public List<ProjectCurrencyEntity> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<ProjectCurrencyEntity> currencies) {
        this.currencies = currencies;
    }

    @Transient
    public List<ProjectTextSnippetEntity> getProjectTextSnippetList() {
        return projectTextSnippetList;
    }

    public void setProjectTextSnippetList(List<ProjectTextSnippetEntity> projectTextSnippetList) {
        this.projectTextSnippetList = projectTextSnippetList;
    }

    @Transient
    public List<ProjectDocumentEntity> getProjectDocumentList() {
        return projectDocumentList;
    }

    public void setProjectDocumentList(List<ProjectDocumentEntity> projectDocumentList) {
        this.projectDocumentList = projectDocumentList;
    }

    @Transient
    public List<TextSnippetEntity> getGlobalStandardTextList() {
        return globalStandardTextList;
    }

    public void setGlobalStandardTextList(List<TextSnippetEntity> globalStandardTextList) {
        this.globalStandardTextList = globalStandardTextList;
    }

    @Transient
    public List<MainDocumentEntity> getMainDocumentList() {
        return mainDocumentList;
    }

    public void setMainDocumentList(List<MainDocumentEntity> mainDocumentList) {
        this.mainDocumentList = mainDocumentList;
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

    @Size(max = 1000)
    @Column(name = "invoice_to", length = 1000)
    public String getInvoiceTo() {
        return invoiceTo;
    }

    public void setInvoiceTo(String invoiceTo) {
        this.invoiceTo = invoiceTo;
    }
}

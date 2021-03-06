package ch.swissbytes.fqmes.report;


import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetExWorksService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJobSummaryService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetPlannerService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetReceivableManifestService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.ExpeditingStatusEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.model.entities.VPurchaseOrder;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.fqm.boundary.UserSession;
import ch.swissbytes.fqmes.boundary.purchase.PurchaseOrderViewTbl;
import ch.swissbytes.fqmes.report.util.DocTypeEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 12/06/14.
 */
@Named
@ViewScoped
public class ReportBean implements Serializable {

    private Logger log = Logger.getLogger(ReportBean.class.getName());

    @PersistenceContext(unitName = "fqmPU")
    private EntityManager entityManager;

    @Resource(lookup = "java:/fqm/procurementDS")
    private DataSource dataSource;


    @Inject
    private PurchaseOrderService service;

    @Inject
    private Configuration configuration;

    @Inject
    private SpreadsheetJobSummaryService spreadsheetJobSummaryService;

    @Inject
    private SpreadsheetReceivableManifestService spreadsheetReceivableManifestService;

    @Inject
    private SpreadsheetPlannerService spreadsheetPlannerService;

    @Inject
    private SpreadsheetExWorksService spreadsheetExWorksService;

    @Inject
    private UserSession userSession;

    @Inject
    private UserRoleService userRoleService;

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    private Locale locale;

    private Map<String, String> messages;

    private Boolean openReport = false;


    private List<VPurchaseOrder> selected;


    private String typeId;

    private boolean isAllProviders;

    private StreamedContent file;

    private String expeditingStatuses = "";

    public Boolean getOpenReport() {
        return openReport;
    }

    @PostConstruct
    public void init() {
        log.info("ReportBean init!");
        selected = new ArrayList<>();
    }

    public List<VPurchaseOrder> getSelected() {
        return selected;
    }

    public void addPurchaseOrder(final Long id) {
        log.info("public void addPurchaseOrder(final Long id=" + id + ")");
        boolean isAdded = false;
        for (VPurchaseOrder po : selected) {
            if (po.getPoId().longValue() == id.longValue()) {
                isAdded = true;
                break;
            }
        }
        if (!isAdded) {
            VPurchaseOrder po = service.findVPOById(id);
            if (po != null) {
                selected.add(po);
            }
        }
    }

    public void addAllPOFiltered(PurchaseOrderViewTbl list) {
        log.info("all filtered");
        cleanPurchaseSelected();
        List<VPurchaseOrder> poList = list.getAllFiltered();
        for (VPurchaseOrder po : poList) {
            selected.add(po);
        }
    }

    public void cleanPurchaseSelected() {
        selected = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.info("Report Bean destroyed!");
    }

    public void printReportReceivableManifest() {
        log.info("Printing Receivable Manifest Report");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportPurchaseOrder("/receivableManifest/receivableManifest", "Receivable.Manifest", messages, locale, entityManager, collectIds(), configuration, DocTypeEnum.PDF,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printReportJobSummary() {
        log.info("Printing Job Summary Report");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportPurchaseOrder("/jobSummary/JobSummary", "job.summary", messages, locale, entityManager, collectIds(), configuration, DocTypeEnum.PDF,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printPlannerReport(){
        log.info("Printing Planner Report");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportPurchaseOrder("/planner/plannerReport", "planner.report", messages, locale, entityManager, collectIds(), configuration, DocTypeEnum.PDF,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public void printReportNextKey() {
        log.info("Printing Next Key Report");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportNextKey("/nextKeyInfo/nextKey", "Next.Key.Info", messages, locale, entityManager, collectIds(), configuration, DocTypeEnum.PDF,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public StreamedContent downloadJobSummaryFileExport() {
        log.info("Downloading Job Summary File Export XLS");
        InputStream stream = spreadsheetJobSummaryService.generateWorkbook(purchaseOrderList());
        file = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "Job.Summary.xlsx");
        return file;
    }

    public StreamedContent downloadReceivableManifestFileExport() {
        log.info("Downloading Receivable Manifest File Export XLS");
        InputStream stream = spreadsheetReceivableManifestService.generateWorkbook(purchaseOrderList());
        file = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "Receivable.Manifest.xlsx");
        return file;
    }

    public StreamedContent downloadPlannerReportFileExport() {
        log.info("Downloading Planner File Export XLS");
        InputStream stream = spreadsheetPlannerService.generateWorkbook(purchaseOrderList());
        file = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "Planner.xlsx");
        return file;
    }

    public StreamedContent downloadExWorksReportFileExport() {
        log.info("Downloading Ex Works Report File Export XLS");
        InputStream stream = spreadsheetExWorksService.generateWorkbook(purchaseOrderList());
        file = new DefaultStreamedContent(stream, "application/vnd.ms-excel", "ExWorks.xlsx");
        return file;
    }

    public void printReportDashboard(Map<String, String> parameterDashboard) {
        log.info("Printing Dashboard Report");
        openReport = false;
        initializeParametersToJasperReport();
        ReportView reportView = new ReportDashboard("/Dashboard/dashboard", "Dashboard.Expediting", messages, locale, entityManager, configuration, parameterDashboard,dataSource);
        reportView.printDocument(null);
        openReport = true;
    }

    public List<Long> collectIds() {
        List<Long> ids = new ArrayList<>();
        for (VPurchaseOrder vpo : selected) {
            ids.add(vpo.getPoId());
        }
        return ids;
    }

    public List<PurchaseOrderEntity> purchaseOrderList() {
        List<PurchaseOrderEntity> list = new ArrayList<>();
        for (VPurchaseOrder vpo : selected) {
            PurchaseOrderEntity po = service.findByIdOnly(vpo.getPoId());
            list.add(po);
        }
        return list;
    }


    private void initializeParametersToJasperReport() {
        locale = new Locale(Locale.ENGLISH.getLanguage());
        messages = new HashMap<String, String>();
    }

    public String loadPurchaseOrderStatuses(final Long purchaseOrderId) {
        expeditingStatuses = "";
        if (purchaseOrderId != null) {
            List<ExpeditingStatusEntity> expeditingStatusList = service.findExpeditingStatusByPOid(purchaseOrderId);
            for (ExpeditingStatusEntity ex : expeditingStatusList) {
                expeditingStatuses = expeditingStatuses + ex.getPurchaseOrderStatus().ordinal() + ",";
            }
            if (expeditingStatuses.length() > 0) {
                expeditingStatuses = expeditingStatuses.substring(0, expeditingStatuses.length() - 1);
                String[] ids = expeditingStatuses.split(",");
                String expStatuses = "";
                for (int i = 0; i < ids.length; i++) {
                    String exStatus = bundle.getString("postatus." + ExpeditingStatusEnum.getEnum(Integer.valueOf(ids[i]).intValue()).name());
                    expStatuses = expStatuses + exStatus + ", ";
                }
                expeditingStatuses = expStatuses;
                expeditingStatuses = expeditingStatuses.substring(0, expeditingStatuses.length() - 2);
            }

        }
        return expeditingStatuses;
    }

    public boolean canAccessToExWorksReport(){
        UserRoleEntity userRoleEntity = userRoleService.findByUserIdAndModuleSystem(userSession.getCurrentUser().getId(), ModuleSystemEnum.EXPEDITING);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> roleEntity = " + userRoleEntity.getRole().getName());
        if(RoleEnum.ADMINISTRATOR.equalsTo(userRoleEntity.getRole().getId()) || RoleEnum.SENIOR.equalsTo(userRoleEntity.getRole().getId())){
            return true;
        }
        return false;
    }


    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }


    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }


    public boolean isAllProviders() {
        return isAllProviders;
    }

    public void setAllProviders(boolean isAllProviders) {
        this.isAllProviders = isAllProviders;
    }

    public StreamedContent getFile() {
        return file;
    }

    public String getExpeditingStatuses() {
        return expeditingStatuses;
    }

    public void setExpeditingStatuses(String expeditingStatuses) {
        this.expeditingStatuses = expeditingStatuses;
    }
}

package ch.swissbytes.fqmes.report;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderViewDao;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.TypeDateReportEnum;
import ch.swissbytes.fqm.boundary.UserSession;
import ch.swissbytes.fqmes.boundary.purchase.PurchaseOrderViewTbl;
import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.procurement.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class ReportListBean implements Serializable {

    @Inject
    private PurchaseOrderViewDao dao;

    @Inject
    private UserSession userSession;

    @Inject
    private ProjectService projectService;

    private PurchaseOrderViewTbl tbl;

    private SearchPurchase searchPurchase = new SearchPurchase();

    private static final Logger log = Logger.getLogger(ReportListBean.class.getName());

    private String mode;
    private Long projectId;
    private Integer typeDateId;
    private Long startDate;
    private Long endDate;

    @PostConstruct
    public void create() {
        log.info("creating bean purchase list");
        log.log(Level.FINER, "FINER log");
        searchPurchase = new SearchPurchase();
        List<Long> projectsAssignIds = new ArrayList<>();
        List<ProjectEntity> projects = projectService.findByPermissionForUser(userSession.getCurrentUser().getId());
        for (ProjectEntity p : projects) {
            projectsAssignIds.add(p.getId());
        }
        searchPurchase.getProjectsAssignedId().addAll(projectsAssignIds);
        tbl = new PurchaseOrderViewTbl(dao, searchPurchase);

    }

    private void search() {
        log.info("search...");
        fixedStatusesTerms();
        tbl = new PurchaseOrderViewTbl(dao, searchPurchase);
    }

    private void fixedStatusesTerms() {
        if (searchPurchase.getStatuses() != null && searchPurchase.getStatuses().length() > 1) {
            searchPurchase.setStatuses(searchPurchase.getStatuses().replaceAll("\\s", ""));
            String status1 = searchPurchase.getStatuses().substring(0, 1);
            try {
                Integer i = Integer.parseInt(status1);
            } catch (NumberFormatException nfe) {
                searchPurchase.setStatuses(searchPurchase.getStatuses().substring(1, searchPurchase.getStatuses().length()));
            }
        }
    }

    public void loadMultipleStatuses(){
        if(StringUtils.isNotEmpty(mode)) {
            switch (mode) {
                case "total":
                    searchPurchase.setStatuses("0,2,3,4,5,6,7,8,9,10,11,13,14,15,16,17");
                    break;
                case "open":
                    searchPurchase.setStatuses("0,2,3,4,6,7,8,9,10,11,13,14,15,16,17");
                    break;
                case "completed":
                    searchPurchase.setStatuses("5");
                    break;
                case "next1":
                    searchPurchase.setStatuses("0,2,3,4,6,7,8,9,11,13,14,15,16,17");
                    searchPurchase.setTypeDateReport(TypeDateReportEnum.getEnum(typeDateId));
                    DateTime startDateTime = new DateTime(startDate);
                    DateTime endDateTime = new DateTime(endDate);
                    searchPurchase.setStartDateReport(startDateTime.toDate());
                    searchPurchase.setEndDateReport(endDateTime.toDate());
                    break;
                case "next3":
                    searchPurchase.setStatuses("0,2,3,4,6,7,8,9,11,13,14,15,16,17");
                    searchPurchase.setTypeDateReport(TypeDateReportEnum.getEnum(typeDateId));
                    DateTime startDateTime2 = new DateTime(startDate);
                    DateTime endDateTime2 = new DateTime(endDate);
                    searchPurchase.setStartDateReport(startDateTime2.toDate());
                    searchPurchase.setEndDateReport(endDateTime2.toDate());
                    break;
                case "mrrs":
                    searchPurchase.setStatuses("10");
                    break;
                default:
                    break;
            }
            ProjectEntity entity = projectService.findById(projectId);
            searchPurchase.setProject(entity!=null?entity.getProjectNumber():"");
        }
    }

    public List<TypeDateReportEnum> getTypesDateList() {
        List<TypeDateReportEnum> typesDateList = Arrays.asList(TypeDateReportEnum.values());
        return typesDateList;
    }

    public String typeDateName(final Integer Id) {
        return TypeDateReportEnum.valueOf(Id).getLabel();
    }

    public boolean verifyTypeDateReport(Integer typeDateId) {
        if(searchPurchase.getTypeDateReport()!=null) {
            return searchPurchase.getTypeDateReport().getId().intValue() == typeDateId.intValue();
        }else{
            return false;
        }
    }

    public void resetNextKeyAndForecast() {
        searchPurchase.setStartDateReport(null);
        searchPurchase.setEndDateReport(null);
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying bean");
    }

    public PurchaseOrderViewTbl getList() {
        return tbl;
    }

    public void doSearch() {
        log.info("doSearch");
        search();
    }

    public void doClean() {
        searchPurchase.clean();
        search();
    }

    public SearchPurchase getSearchPurchase() {
        return searchPurchase;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getTypeDateId() {
        return typeDateId;
    }

    public void setTypeDateId(Integer typeDateId) {
        this.typeDateId = typeDateId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}

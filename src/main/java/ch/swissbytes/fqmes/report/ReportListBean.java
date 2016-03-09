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
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

    public List<String> getTypesDate() {
        List<String> list = new ArrayList<>();
        list.add("Delivery Date");
        list.add("Forecast Ex Works Date");
        list.add("Actual Ex Works Date");
        list.add("Forecast Site Date");
        list.add("Actual Site Date");
        list.add("Required Site Date");
        list.add("Required On Site Date");
        list.add("Actual On Site Date");
        list.add("Next Key Date");
        return list;
    }

    public List<TypeDateReportEnum> getTypesDateList() {
        List<TypeDateReportEnum> typesDateList = Arrays.asList(TypeDateReportEnum.values());
        return typesDateList;
    }

    public String typeDateName(final Integer Id) {
        return TypeDateReportEnum.valueOf(Id).getLabel();
    }

    public boolean verifyTypeDateReport(Integer typeDateId) {
        return searchPurchase.getTypeDateReport().getId().intValue() == typeDateId.intValue();
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

}

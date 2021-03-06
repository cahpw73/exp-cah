package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectUser.ProjectUserService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderDao;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderViewDao;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.ProjectUserEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.VPurchaseOrder;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.fqm.boundary.UserSession;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class PurchaseOrderListBean implements Serializable {

    @Inject
    private PurchaseOrderViewDao dao;

    @Inject
    private PurchaseOrderDao poDao;

    @Inject
    private PurchaseOrderService purchaseOrderService;

    @Inject
    private UserSession userSession;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectUserService projectUserService;

    private static final String filterAllProjectStr = "all";

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    private PurchaseOrderViewTbl vTbl;

    private SearchPurchase searchPurchase;

    private Long purchaseOrderId;

    private PurchaseOrderEntity purchaseOrderSelected;

    private static final Logger log = Logger.getLogger(PurchaseOrderListBean.class.getName());

    private String PREFIX = "v";

    private String scrollTop = "0";

    private Long poId;

    private String projectName;


    @PostConstruct
    public void create() {
        log.info("creating bean purchase list");
        searchPurchase = new SearchPurchase();
        List<Long> projectsAssignIds = new ArrayList<>();
        List<ProjectEntity> projects = projectService.findByPermissionForUser(userSession.getCurrentUser().getId());
        for (ProjectEntity p : projects) {
            projectsAssignIds.add(p.getId());
        }
        searchPurchase.getProjectsAssignedId().addAll(projectsAssignIds);
        vTbl = new PurchaseOrderViewTbl(dao, searchPurchase);
    }

    public void posLoad(){
        log.info("projectName= "+(StringUtils.isEmpty(projectName)?"null":projectName));
        searchPurchase.setProject(projectName);
        search();
    }

    private void search() {
        log.info("search..." + searchPurchase);
        vTbl = new PurchaseOrderViewTbl(dao, searchPurchase);
    }

    public void selectPurchaseOrderId(final Long purchaseOrderId) {
        log.info("selectPurchaseOrderId(purchaseOrderId[" + purchaseOrderId + "])");
        this.purchaseOrderId = purchaseOrderId;
        purchaseOrderSelected = dao.findById(PurchaseOrderEntity.class, purchaseOrderId).get(0);
        log.info("Purchase Selected: " + purchaseOrderSelected.getProject());
    }

    public void selectPurchaseOrder() {
        log.info("selectPurchaseOrder()");
        log.info("purchaseOrderId: " + purchaseOrderId);
        purchaseOrderSelected = dao.findById(PurchaseOrderEntity.class, purchaseOrderId).get(0);
        log.info("Purchase Selected: " + purchaseOrderSelected.getProject());
    }

    public void doDeletePurchaseOrder() {
        purchaseOrderService.doDelete(purchaseOrderId);
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying bean");
    }

    public PurchaseOrderViewTbl getListView() {
        return vTbl;
    }

    public void doSearch() {
        log.info("doSearch");
        search();
    }

    public String doClean() {
        searchPurchase.clean();
        search();
        return "list?faces-redirect=true";
    }

    public String addPrefixToVariation(VPurchaseOrder po) {
        String variation = "";
        if (po != null && StringUtils.isNotEmpty(po.getVariation()) && StringUtils.isNotBlank(po.getVariation())) {
            variation = PREFIX + po.getVariation();
        }
        return variation;

    }

    public String loadStatusNameById(String ids) {
        String statusStr = "";
        if (ids.length() > 1) {
            String[] statusIds = ids.split(",");
            for (int i = 0; i < statusIds.length; i++) {
                if(StringUtils.isNotEmpty(ids)) {
                    Integer expeditingStatusId = Integer.valueOf(statusIds[i]);
                    ExpeditingStatusEnum expeditingStatusEnum = ExpeditingStatusEnum.getEnum(expeditingStatusId);
                    statusStr = statusStr + bundle.getString("postatus." + expeditingStatusEnum.name())+", ";
                }
            }
        } else {
            if(StringUtils.isNotEmpty(ids)) {
                Integer expeditingStatusId = Integer.valueOf(ids);
                ExpeditingStatusEnum expeditingStatusEnum = ExpeditingStatusEnum.getEnum(expeditingStatusId);
                statusStr = statusStr + bundle.getString("postatus." + expeditingStatusEnum.name())+", ";
            }
        }
        if(statusStr.length()>1) {
            statusStr = statusStr.substring(0, statusStr.length() - 2);
        }
        return statusStr;
    }

    public List<String> projectAssigned(){
        Long userId = userSession.getCurrentUser().getId();
        List<ProjectUserEntity> list = projectUserService.findByUserId(userId);
        List<String> projects = new ArrayList<>();
        projects.add(filterAllProjectStr);
        for(ProjectUserEntity pj : list){
            projects.add(pj.getProject().getProjectNumber());
        }
        return projects;
    }


    @Produces
    @Named
    @RequestScoped
    public SearchPurchase getSearchPurchase() {
        return searchPurchase;
    }

    public PurchaseOrderEntity getPurchaseOrderSelected() {
        return purchaseOrderSelected;
    }

    public void setPurchaseOrderSelected(PurchaseOrderEntity purchaseOrderSelected) {
        this.purchaseOrderSelected = purchaseOrderSelected;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        log.info("setPurchaseOrderId(Long purchaseOrderId[" + purchaseOrderId + "])");
        this.purchaseOrderId = purchaseOrderId;
        purchaseOrderSelected = dao.findById(PurchaseOrderEntity.class, purchaseOrderId).get(0);
        log.info("Purchase Selected: " + purchaseOrderSelected.getProject());
    }

    public String redirectCreatePurchaseOrder() {
        return "/purchase/create?faces-redirect=true";
    }

    public String redirectToEdit() {
        log.info("redirectToEdit() searchPurchase.Project= " + (searchPurchase.getProject()!=null?searchPurchase.getProject():"null"));
        if(!StringUtils.isEmpty(searchPurchase.getProject())){
            log.info("/purchase/edit.xhtml?faces-redirect=true&poId=" + poId + "&anchor=" + scrollTop+"&project="+searchPurchase.getProject());
            return "/purchase/edit.xhtml?faces-redirect=true&poId=" + poId + "&anchor=" + scrollTop+"&project="+searchPurchase.getProject();
        }else {
            log.info("/purchase/edit.xhtml?faces-redirect=true&poId=" + poId + "&anchor=" + scrollTop+"&project=all");
            return "/purchase/edit.xhtml?faces-redirect=true&poId=" + poId + "&anchor=" + scrollTop+"&project=all";
        }
    }

    public String redirectToView() {
        log.info("redirectToView() searchPurchase.Project= " + (searchPurchase.getProject()!=null?searchPurchase.getProject():"null"));
        if(!StringUtils.isEmpty(searchPurchase.getProject())){
            return "/purchase/view.xhtml?faces-redirect=true&poId=" + poId + "&anchor=" + scrollTop+"&project="+searchPurchase.getProject();
        }else {
            return "/purchase/view.xhtml?faces-redirect=true&poId=" + poId + "&anchor=" + scrollTop + "&project=all";
        }
    }

    public String getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(String scrollTop) {
        this.scrollTop = scrollTop;
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

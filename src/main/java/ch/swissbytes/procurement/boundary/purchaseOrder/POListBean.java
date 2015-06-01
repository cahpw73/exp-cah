package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 6/1/2015.
 */

@Named
@ViewScoped
public class PoListBean implements Serializable {

    private static final Logger log = Logger.getLogger(PoListBean.class.getName());

    private List<PurchaseOrderEntity> list;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private ProjectService projectService;

    private String projectId;


    @PostConstruct
    public void create(){
        log.info("Created POListBean");
        list=new ArrayList<>();
    }

    public void load(){
        if(StringUtils.isNotEmpty(projectId)&& StringUtils.isNotBlank(projectId)){
            try{
                Long.parseLong(projectId);
            }catch (NumberFormatException nfe){
             throw   new IllegalArgumentException("project Id invalid");
            }
            ProjectEntity projectEntity=projectService.findProjectById(Long.parseLong(projectId));
            if(projectEntity==null){
                throw   new IllegalArgumentException("project Id invalid");
            }
            list=service.purchaseListByProject(Long.parseLong(projectId));
        }else{
            throw   new IllegalArgumentException("project Id invalid");
        }

    }

    @PreDestroy
    public void destroy(){
        log.info("Destroyed POListBean");
    }

    public List<PurchaseOrderEntity> getList() {
        return list;
    }


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}

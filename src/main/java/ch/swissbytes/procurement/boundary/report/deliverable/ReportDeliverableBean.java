package ch.swissbytes.procurement.boundary.report.deliverable;

import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
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
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 12-06-2015
 */
@Named
@ViewScoped
public class ReportDeliverableBean implements Serializable {

    public static final Logger log = Logger.getLogger(ReportDeliverableBean.class.getName());

    @Inject
    private ProjectService projectService;

    @Inject
    private PurchaseOrderService purchaseOrderService;

    @Inject
    private DeliverableDao deliverableDao;

    private List<ProjectEntity> projectList;

    private List<PurchaseOrderEntity> purchaseOrderList;

    private ProjectEntity selectedProject;

    private String termsPoNo;


    private List<DeliverableDto> deliverableDtoList;

    @PostConstruct
    public void create(){
        log.info("creating reportDeliverableBean");
        selectedProject = new ProjectEntity();
        projectList = new ArrayList<>();
        purchaseOrderList = new ArrayList<>();
        deliverableDtoList = new ArrayList<>();
        loadProjects();
    }

    @PreDestroy
    public void destroy(){
        log.info("destroyed reportDeliverableBean");
    }

    public void loadProjects(){
        log.info("loading projects");
        projectList = projectService.findAllProjects();
        log.info("finalize load");
    }

    public void searchPurchaseOrder(){
        log.info("searching purchase order");
        purchaseOrderList = purchaseOrderService.purchaseListByProject(selectedProject.getId());
        for(PurchaseOrderEntity p : purchaseOrderList){
            p.getPoEntity().getDeliverables().addAll(deliverableDao.findDeliverableByPurchaseOrder(p.getPoEntity().getId()));
        }
        for(PurchaseOrderEntity p : purchaseOrderList){
            for(DeliverableEntity d : p.getPoEntity().getDeliverables()){
                DeliverableDto dto = new DeliverableDto();
                dto.setPoNo(p.getPoEntity().getOrderNumber());
                dto.setVarNo(p.getPoEntity().getVarNumber());
                dto.setPoDescription(p.getPoEntity().getDeliveryInstruction());
                dto.setDescription(d.getDescription());
                dto.setDelNo("");
                dto.setQty(d.getQuantity().toString());
                dto.setRequiredDate(d.getRequiredDate());
                dto.setForecastDate(new Date());
                dto.setReceivedDate(new Date());
                deliverableDtoList.add(dto);
            }
        }
    }

    public void filterDeliverableDtoListByPoNo(){
        log.info("filtering deliverables");
        if(StringUtils.isNotEmpty(termsPoNo) && StringUtils.isNotBlank(termsPoNo)){
            List<DeliverableDto> deliverableDtos = new ArrayList<>();
            for(DeliverableDto dto : deliverableDtoList){
                if(dto.getPoNo().equalsIgnoreCase(termsPoNo.trim())){
                    DeliverableDto deliverableDto = new DeliverableDto();
                    deliverableDtos.add(deliverableDto);
                }
            }
            deliverableDtoList.clear();
            deliverableDtoList.addAll(deliverableDtos);
        }
    }

    public ProjectEntity getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(ProjectEntity selectedProject) {
        this.selectedProject = selectedProject;
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public List<DeliverableDto> getDeliverableDtoList() {
        return deliverableDtoList;
    }

    public String getTermsPoNo() {
        return termsPoNo;
    }

    public void setTermsPoNo(String termsPoNo) {
        this.termsPoNo = termsPoNo;
    }
}

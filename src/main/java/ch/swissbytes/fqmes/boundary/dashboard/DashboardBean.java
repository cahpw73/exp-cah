package ch.swissbytes.fqmes.boundary.dashboard;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.procurement.util.DateUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by Christian on 11/12/2015.
 */
@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private static final Logger log = Logger.getLogger(DashboardBean.class.getName());

    @Inject
    private ProjectService projectService;

    @Inject
    private PurchaseOrderService poService;

    private ProjectEntity projectSelected;

    private List<ProjectEntity> projectList;

    private String totalOfPOs;

    private String completedPOs;
    private String numberCompletedPOs;
    private String percentageCompletedPOs;

    private String openPOs;
    private String numberOpenPOs;
    private String percentageOpenPOs;

    private String deliveryNextMoth;

    private String deliveryNext3Moth;

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");


    @PostConstruct
    public void init() {
        log.info("Created DashboardBean");
        projectList = projectService.findAllProjects();
        loadDataOfDashboard();
    }

    @PreDestroy
    public void destroy() {

    }

    private void loadDataOfDashboard() {
        totalOfPOs = String.valueOf(poService.getTotalNumberOfPOs(projectSelected != null ? projectSelected.getId() : -1));
        loadNumberCompletedPOs();
        loadNumberOpenPOs();
        loadNumberDeliveryNextMoth();
        loadNumberDeliveryNext3Moth();
    }

    private void loadNumberDeliveryNext3Moth() {
        Date deliveryDateIni = DateUtil.getNextNMoth(3);
        Date deliveryDateEnd = DateUtil.getLastDayOfMoth(deliveryDateIni);
        Long projectId = projectSelected != null ? projectSelected.getId() : -1;
        deliveryNext3Moth = String.valueOf(poService.getNumberDeliveryNextMoth(projectId,deliveryDateIni,deliveryDateEnd));
    }

    private void loadNumberDeliveryNextMoth() {
        Date deliveryDateIni = DateUtil.getNextNMoth(1);
        Date deliveryDateEnd = DateUtil.getLastDayOfMoth(deliveryDateIni);
        Long projectId = projectSelected != null ? projectSelected.getId() : -1;
        deliveryNextMoth = String.valueOf(poService.getNumberDeliveryNextMoth(projectId,deliveryDateIni,deliveryDateEnd));
    }

    private void loadNumberOpenPOs() {
        numberOpenPOs = String.valueOf(poService.getNumberOfOpenPOs(projectSelected != null ? projectSelected.getId() : -1));
        Double percentage = (Double.parseDouble(numberOpenPOs) / Double.parseDouble(totalOfPOs)) * 100;
        if (!percentage.isNaN()){
            percentage = Math.round(percentage*100.0)/100.0;
            percentageOpenPOs = String.valueOf(percentage) + "%";
            openPOs = numberOpenPOs + " / " + percentageOpenPOs;
        }else{
            openPOs = "0 / 0.0%";
        }
    }

    private void loadNumberCompletedPOs(){
        numberCompletedPOs = String.valueOf(poService.getNumberOfCompletedPOs(projectSelected != null ? projectSelected.getId() : -1));
        Double percentage = (Double.parseDouble(numberCompletedPOs) / Double.parseDouble(totalOfPOs)) * 100;
        if (!percentage.isNaN()){
            percentage = Math.round(percentage*100.0)/100.0;
            percentageCompletedPOs = String.valueOf(percentage) + "%";
            completedPOs = numberCompletedPOs + " / " + percentageCompletedPOs;
        }else{
            completedPOs = "0 / 0.0%";
        }
    }

    public void refreshDataOfDashboard() {
        loadDataOfDashboard();
    }

    public String getTitleDashboard() {
        String bundleStr = bundle.getString("dashboard.main.title");
        String projectStr = projectSelected != null ? projectSelected.getProjectNumber() : "";
        return bundleStr + " " + projectStr;
    }

    public ProjectEntity getProjectSelected() {
        return projectSelected;
    }

    public void setProjectSelected(ProjectEntity projectSelected) {
        this.projectSelected = projectSelected;
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public String getTotalOfPOs() {
        return totalOfPOs;
    }

    public void setTotalOfPOs(String totalOfPOs) {
        this.totalOfPOs = totalOfPOs;
    }

    public String getNumberCompletedPOs() {
        return numberCompletedPOs;
    }

    public void setNumberCompletedPOs(String numberCompletedPOs) {
        this.numberCompletedPOs = numberCompletedPOs;
    }

    public String getPercentageCompletedPOs() {
        return percentageCompletedPOs;
    }

    public void setPercentageCompletedPOs(String percentageCompletedPOs) {
        this.percentageCompletedPOs = percentageCompletedPOs;
    }

    public String getCompletedPOs() {
        return completedPOs;
    }

    public void setCompletedPOs(String completedPOs) {
        this.completedPOs = completedPOs;
    }

    public String getNumberOpenPOs() {
        return numberOpenPOs;
    }

    public void setNumberOpenPOs(String numberOpenPOs) {
        this.numberOpenPOs = numberOpenPOs;
    }

    public String getPercentageOpenPOs() {
        return percentageOpenPOs;
    }

    public void setPercentageOpenPOs(String percentageOpenPOs) {
        this.percentageOpenPOs = percentageOpenPOs;
    }

    public String getOpenPOs() {
        return openPOs;
    }

    public void setOpenPOs(String openPOs) {
        this.openPOs = openPOs;
    }

    public String getDeliveryNextMoth() {
        return deliveryNextMoth;
    }

    public void setDeliveryNextMoth(String deliveryNextMoth) {
        this.deliveryNextMoth = deliveryNextMoth;
    }

    public String getDeliveryNext3Moth() {
        return deliveryNext3Moth;
    }

    public void setDeliveryNext3Moth(String deliveryNext3Moth) {
        this.deliveryNext3Moth = deliveryNext3Moth;
    }
}

package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.procurement.boundary.Bean;
import ch.swissbytes.procurement.boundary.currency.CurrencyBean;
import ch.swissbytes.procurement.boundary.menu.MainMenuBean;
import ch.swissbytes.procurement.boundary.purchaseOrder.PoTextBean;
import ch.swissbytes.procurement.boundary.textSnippet.TextSnippetBean;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

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
 * Created by Christian on 29/01/2016.
 */
@Named
@ViewScoped
public class DocumentBean extends Bean implements Serializable {

    public static final Logger log = Logger.getLogger(DocumentBean.class.getName());

    @Inject
    private MainDocumentService mainDocumentService;

    @Inject
    private ProjectDocumentService projectDocumentService;

    private List<MainDocumentEntity> mainDocumentList;

    private List<MainDocumentEntity> selectedMainDocList;

    private List<ProjectDocumentEntity> projectDocumentList;

    private List<ProjectDocumentEntity> selectedProjectDocList;

    private ProjectDocumentEntity selectedProjectDoc;

    private ProjectDocumentEntity projectDocument;

    private Long temporaryProjectDocId = -1L;

    private Long tempMainDocId= -1L;

    private String searchTerm;

    private ProjectEntity projectEntity;

    private Long projectEntityId;


    @PostConstruct
    public void init() {
        log.info("DocumentBean created");
        mainDocumentList = new ArrayList<>();
        projectDocumentList = new ArrayList<>();
        selectedMainDocList = new ArrayList<>();
        selectedProjectDocList = new ArrayList<>();
        selectedProjectDoc = new ProjectDocumentEntity();
    }

    @PreDestroy
    public void destroy() {
        log.info("DocumentBean destroying");
    }

    public void loadMainDocumentsEdit(final Long projectId){
        projectDocumentList = projectDocumentService.findByProjectId(projectId);
        mainDocumentList = mainDocumentService.findByProjectId();
        List<MainDocumentEntity> mainDocumentAuxList = new ArrayList<>();
        for (ProjectDocumentEntity pd : projectDocumentList) {
            for (MainDocumentEntity md : mainDocumentList) {
                if (pd.getMainDocumentEntity() != null && pd.getMainDocumentEntity().getId().intValue() == md.getId().intValue()) {
                    mainDocumentAuxList.add(md);
                }
            }
        }
        mainDocumentList.removeAll(mainDocumentAuxList);
    }

    public void loadMainDocumentsCreate(final Long projectId){
        mainDocumentList = mainDocumentService.findByProjectId();
    }

    public void addToProjectText() {
        log.info("add main doc template to project");
        for(MainDocumentEntity md : selectedMainDocList){
            ProjectDocumentEntity entity = new ProjectDocumentEntity();
            entity.setId(temporaryProjectDocId);
            entity.setCode(md.getCode());
            entity.setDescription(md.getDescription());
            entity.setLastUpdate(new Date());
            entity.setMainDocumentEntity(md);
            entity.setStatus(StatusEnum.ENABLE);
            projectDocumentList.add(entity);
            temporaryProjectDocId--;
        }
        mainDocumentList.removeAll(selectedMainDocList);
        selectedMainDocList.clear();
    }

    public void removeFromProjectText() {
        log.info("remove projectDocument from project");
        List<ProjectDocumentEntity> auxProjectList = new ArrayList<>();
        for(ProjectDocumentEntity pd : selectedProjectDocList){
            if(pd.getMainDocumentEntity() != null) {
                mainDocumentList.add(mainDocumentService.findById(pd.getMainDocumentEntity().getId()));
                if(pd.getId()>0){
                    for (ProjectDocumentEntity pe : projectDocumentList){
                        if(pd.getId().intValue() == pe.getId().intValue()){
                            pe.setStatus(StatusEnum.DELETED);
                        }
                    }
                }else{
                    auxProjectList.add(pd);
                }
            }else{
                MainDocumentEntity mainDocument = new MainDocumentEntity();
                mainDocument.setId(tempMainDocId);
                mainDocument.setDescription(pd.getDescription());
                mainDocument.setCode(pd.getCode());
                mainDocumentList.add(mainDocument);
                tempMainDocId--;
                if(pd.getId()>0){
                    for (ProjectDocumentEntity pe : projectDocumentList){
                        if(pd.getId().intValue() == pe.getId().intValue()){
                            pe.setStatus(StatusEnum.DELETED);
                        }
                    }
                }else {
                    auxProjectList.add(pd);
                }
            }
        }
        projectDocumentList.removeAll(auxProjectList);
        selectedProjectDocList.clear();
    }

    public void prepareProjectDocListToSave(){
        for (ProjectDocumentEntity p : projectDocumentList) {
            p.setId(null);
        }
    }

    public void editItem(ProjectDocumentEntity entity) {
        log.info("edit item");
        entity.startEditing();
        entity.storeOldValue(entity);
    }

    public void confirmItem(ProjectDocumentEntity entity) {
        log.info("confirm text");
        int index = projectDocumentList.indexOf(entity);
        projectDocumentList.set(index, entity);
        entity.stopEditing();
    }

    public void deleteItem(ProjectDocumentEntity entity) {
        log.info("delete item");
        if (entity.getId() < 0L) {
            projectDocumentList.remove(entity);
        } else {
            entity.setStatus(StatusEnum.DELETED);
        }
    }

    public void cancelEditionItem(ProjectDocumentEntity entity) {
        log.info("cancel item");
        if (!itemNoIsNotEmpty(entity)) {
            projectDocumentList.remove(entity);
        } else {
            entity.stopEditing();
            entity = entity.getValueCloned();
        }
    }

    private boolean itemNoIsNotEmpty(ProjectDocumentEntity entity) {
        return StringUtils.isNotEmpty(entity.getDescription()) && StringUtils.isNotBlank(entity.getDescription());
    }

    public List<ProjectDocumentEntity> filteredList() {
        List<ProjectDocumentEntity> list = new ArrayList<>();
        for (ProjectDocumentEntity r : projectDocumentList) {
            if (r.getStatus() != null && r.getStatus().getId().intValue() == StatusEnum.ENABLE.getId().intValue()) {
                list.add(r);
            }
        }
        return list;
    }

    public void loadSeletedProjectDoc(ProjectDocumentEntity entity){
        selectedProjectDoc = entity;
    }

    public void updateProjectDocumentDt(){
        for (ProjectDocumentEntity r : projectDocumentList) {
            if (r.getId().intValue() == selectedProjectDoc.getId().intValue()) {
                r.setDescription(selectedProjectDoc.getDescription());
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('projectDocModal').hide();");
    }

    public void saveNewProjectDocument(){
        projectDocument.setProject(projectEntity);
        projectDocument.setMainDocumentEntity(null);
        projectDocumentService.doSave(projectDocument);
        projectDocumentList = projectDocumentService.findByProjectId(projectEntityId);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addProjectDocModal').hide();");
    }

    public void resetProjectDocument(){
        projectDocument = new ProjectDocumentEntity();
    }

    public void doSearchGlobalText() {
        mainDocumentList.clear();
        mainDocumentList = mainDocumentService.findByText(searchTerm);
        List<MainDocumentEntity> auxMainDoc = new ArrayList<>();
        for(ProjectDocumentEntity pd : projectDocumentList){
            for(MainDocumentEntity md : mainDocumentList){
                if(pd.getCode().equals(md.getCode())){
                    auxMainDoc.add(md);
                }
            }
        }
        mainDocumentList.removeAll(auxMainDoc);
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<MainDocumentEntity> getMainDocumentList() {
        return mainDocumentList;
    }

    public List<ProjectDocumentEntity> getProjectDocumentList() {
        return projectDocumentList;
    }

    public List<MainDocumentEntity> getSelectedMainDocList() {
        return selectedMainDocList;
    }

    public List<ProjectDocumentEntity> getSelectedProjectDocList() {
        return selectedProjectDocList;
    }

    public void setSelectedMainDocList(List<MainDocumentEntity> selectedMainDocList) {
        this.selectedMainDocList = selectedMainDocList;
    }

    public void setSelectedProjectDocList(List<ProjectDocumentEntity> selectedProjectDocList) {
        this.selectedProjectDocList = selectedProjectDocList;
    }

    public ProjectDocumentEntity getSelectedProjectDoc() {
        return selectedProjectDoc;
    }

    public void setSelectedProjectDoc(ProjectDocumentEntity selectedProjectDoc) {
        this.selectedProjectDoc = selectedProjectDoc;
    }

    public ProjectDocumentEntity getProjectDocument() {
        return projectDocument;
    }

    public void setProjectDocument(ProjectDocumentEntity projectDocument) {
        this.projectDocument = projectDocument;
    }

    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    public void setProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public Long getProjectEntityId() {
        return projectEntityId;
    }

    public void setProjectEntityId(Long projectEntityId) {
        this.projectEntityId = projectEntityId;
    }

    @Inject
    private TextSnippetService textSnippetService;

    @Inject
    private CurrencyBean currencyBean;

    @Inject
    private TextSnippetBean standartText;

    @Inject
    private PoTextBean poTextBean;

}
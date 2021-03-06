package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.currency.CurrencyService;
import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.procurement.boundary.Bean;
import ch.swissbytes.procurement.boundary.currency.CurrencyBean;
import ch.swissbytes.procurement.boundary.menu.MainMenuBean;
import ch.swissbytes.procurement.boundary.purchaseOrder.PoTextBean;
import ch.swissbytes.procurement.boundary.textSnippet.TextSnippetBean;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;
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
 * Created by Christian on 15/05/2015.
 */
@Named
@ViewScoped
public class ProjectBean extends Bean implements Serializable {

    public static final Logger log = Logger.getLogger(ProjectBean.class.getName());

    @Inject
    private MainMenuBean mainMenuBean;

    @Inject
    private ProjectService projectService;

    @Inject
    private SupplierProcService supplierProcService;

    @Inject
    private LogoService logoService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private TextSnippetService textSnippetService;

    @Inject
    private ProjectTextSnippetService projectTextSnippetService;

    @Inject
    private DocumentBean documentBean;

    private List<SupplierProcEntity> supplierProcList;

    private List<ProjectCurrencyEntity> projectCurrencyList;

    private List<CurrencyEntity> currencyList;

    private List<TextSnippetEntity> globalStandardTextList;

    private List<TextSnippetEntity> selectedGlobalTexts;

    private List<TextSnippetEntity> projectStandardTextList;

    private List<ProjectTextSnippetEntity> projectTextSnippetList;

    private List<ProjectTextSnippetEntity> selectedProjectTexts;

    private List<ProjectTextSnippetEntity> projectTextSnippetListFromPO;

    private ProjectEntity projectEntity;

    private ProjectCurrencyEntity projectCurrencyEntity;

    private boolean isCreateProject;

    private Long projectId;

    private Boolean createCurrency = true;

    private Long temporalCurrencyId = -1L;

    private Long temporaryTextId = -1L;

    private Long tempProjectTextId = -1L;

    private Long tempClausesId = -1L;

    private final String DEFAULT_CURRENCY_FORMAT = "#,###.00";

    private String searchTerm;


    @PostConstruct
    public void init() {
        log.info("ProjectBean created");
        projectEntity = new ProjectEntity();
        projectCurrencyEntity = new ProjectCurrencyEntity();
        projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
        supplierProcList = new ArrayList<>();
        projectCurrencyList = new ArrayList<>();
        currencyList = new ArrayList<>();
        globalStandardTextList = new ArrayList<>();
        projectStandardTextList = new ArrayList<>();
        selectedGlobalTexts = new ArrayList<>();
        projectStandardTextList = new ArrayList<>();
        projectTextSnippetList = new ArrayList<>();
        selectedProjectTexts = new ArrayList<>();
        projectTextSnippetListFromPO = new ArrayList<>();
        loadSupplierProcList();
        loadCurrencyList();

    }

    @PreDestroy
    public void destroy() {
        log.info("ProjectBean destroying");
    }

    public void loadActionCrud() {
        log.info("load action crud");
        if (isCreateProject) {
            loadGlobalStandardTextList();
            documentBean.loadMainDocumentsCreate();
            documentBean.setProjectEntityId(projectId);
        } else {
            projectEntity = projectService.findProjectById(projectId);
            projectCurrencyList = projectService.findProjectCurrencyByProjectId(projectId);
            projectTextSnippetList = projectService.findProjectTextSnippetByProjectIdOnlyProject(projectId);
            loadGlobalStandardTextList();
            loadAllStandardText();
            documentBean.loadMainDocumentsEdit(projectId);
            documentBean.setProjectEntity(projectEntity);
            documentBean.setProjectEntityId(projectId);
        }
    }

    private void loadAllStandardText() {
        for (ProjectTextSnippetEntity pt : projectTextSnippetList) {
            for (TextSnippetEntity ts : globalStandardTextList) {
                if (pt.getTextSnippet()!=null && pt.getTextSnippet().getId().intValue() == ts.getId().intValue()) {
                    projectStandardTextList.add(ts);
                }
            }
        }
        globalStandardTextList.removeAll(projectStandardTextList);
    }

    public void removedStandardTextRepeat(){
        List<TextSnippetEntity> auxTextSnippetList = new ArrayList<>();
        for (ProjectTextSnippetEntity pt : projectTextSnippetList) {
            for (TextSnippetEntity ts : globalStandardTextList) {
                if (pt.getTextSnippet()!=null && pt.getTextSnippet().getId().intValue() == ts.getId().intValue()) {
                    auxTextSnippetList.add(ts);
                }
            }
        }
        globalStandardTextList.removeAll(auxTextSnippetList);
    }

    public String doSave() {
        log.info("do save");
        mainMenuBean.select(0);
        if (dataValidate()) {
            prepareProjectTextAndProjectDocumentToSave();
            collectionAllData();
            projectEntity = projectService.doSave(projectEntity);
            Messages.addFlashGlobalInfo("The project " + projectEntity.getTitle() + " has been saved.");
            return "edit?faces-redirect=true&isCreateProject=false&projectId=" + projectEntity.getId() + "";
        }
        return "";
    }

    public String doUpdate() {
        log.info("do update");
        mainMenuBean.select(0);
        if (dataValidateToUpdate()) {
            collectionAllData();
            projectEntity = projectService.doUpdate(projectEntity);
            Messages.addFlashGlobalInfo("The project " + projectEntity.getTitle() + " has been saved.");
            return "edit?faces-redirect=true&isCreateProject=false&projectId=" + projectEntity.getId() + "";
        }
        return "";
    }

    private void collectionAllData() {
        log.info("collecting data");
        projectEntity.getCurrencies().addAll(projectCurrencyList);
        projectEntity.getProjectTextSnippetList().addAll(projectTextSnippetList);
        projectEntity.getGlobalStandardTextList().addAll(globalStandardTextList);
        projectEntity.getProjectDocumentList().addAll(documentBean.getProjectDocumentList());
        projectEntity.getMainDocumentList().addAll(documentBean.getMainDocumentList());

    }

    public void addCurrency() {
        log.info("add currency");
        if (validateCurrency()) {
            projectCurrencyEntity.setId(temporalId());
            boolean defaultProject = filteredProjectCurrencies().size() == 0 ? true : false;
            projectCurrencyEntity.setProjectDefault(defaultProject);
            projectCurrencyEntity.setLastUpdate(new Date());
            projectCurrencyEntity.setStatus(StatusEnum.ENABLE);
            projectCurrencyList.add(projectCurrencyEntity);
            projectCurrencyEntity = new ProjectCurrencyEntity();
            projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
        }
    }

    private boolean validateCurrency() {
        boolean validated = true;
        if(projectCurrencyEntity.getCurrency() !=null) {
            for (ProjectCurrencyEntity p : projectCurrencyList) {
                if (p.getCurrency().getId().longValue() == projectCurrencyEntity.getCurrency().getId().longValue()) {
                    if(projectCurrencyEntity.getId()==null||(projectCurrencyEntity.getId()!=null &&p.getId().longValue()!=projectCurrencyEntity.getId().longValue()) ){
                        Messages.addFlashError("currencyId", "Currency already added");
                        validated = false;
                        break;
                    }
                }
            }
        }
        if (StringUtils.isEmpty(projectCurrencyEntity.getFormat()) && StringUtils.isBlank(projectCurrencyEntity.getFormat())) {
            Messages.addError("format", "Please enter Format");
            validated = false;
        }
        if (projectCurrencyEntity.getCurrencyFactor() == null) {
            Messages.addError("currencyFactor", "Please enter Currency Factor");
            validated = false;
        }
        if (projectCurrencyEntity.getExchangeRate() == null) {
            Messages.addError("exchangeRate", "Please enter Exchange Rate");
            validated = false;
        }
        if (projectCurrencyEntity.getCurrency() == null) {
            Messages.addError("currencyId", "Please enter Currency");
            validated = false;
        }
        return validated;
    }

    private Long temporalId() {
        temporalCurrencyId = temporalCurrencyId - 1;
        return temporalCurrencyId;
    }

    public void updateCurrencyList() {
        log.info("update currency list");
        if (validateCurrency()) {
            projectCurrencyEntity.setLastUpdate(new Date());
            for (ProjectCurrencyEntity pc : projectCurrencyList) {
                if (pc.equals(projectCurrencyEntity)) {
                    pc = projectCurrencyEntity;
                }
            }
            projectCurrencyEntity = new ProjectCurrencyEntity();
            createCurrency = true;
            projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
        }else{

        }
    }

    public void cancelCurrencyOperation() {
        log.info("cancel currency operation");
        projectCurrencyEntity = new ProjectCurrencyEntity();
        createCurrency = true;
        projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
    }

    public void doDeleteCurrency(ProjectCurrencyEntity projectCurrency) {
        int index = projectCurrencyList.indexOf(projectCurrency);
        if (index >= 0) {
            if (projectCurrencyList.get(index).getId() < 0) {
                projectCurrencyList.remove(index);
            } else {
                projectCurrencyList.get(index).setStatus(StatusEnum.DELETED);
                projectCurrencyList.get(index).setLastUpdate(new Date());
            }
        }
        projectCurrencyEntity = new ProjectCurrencyEntity();
        createCurrency = true;
        projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
    }

    public void doUpdateCurrency(ProjectCurrencyEntity projectCurrency) {
        projectCurrencyEntity = projectCurrency;
        createCurrency = false;
    }

    private boolean dataValidate() {
        boolean result = true;
        if (projectService.existsProjectNumber(projectEntity.getProjectNumber())) {
            Messages.addFlashError("projectNumber", "Project number was already registered!");
            result = false;
        }
        if (filteredProjectCurrencies().size() == 0) {
            Messages.addFlashGlobalError("You must add at least 1 currency");
            result = false;
        }
        return result;
    }

    private boolean dataValidateToUpdate() {
        boolean result = true;
        if (projectService.validateDuplicityProjectNumber(projectEntity.getProjectNumber(), projectEntity.getId())) {
            Messages.addError("projectNumber", "Project number was already registered!");
            result = false;
        }
        if (filteredProjectCurrencies().size() == 0) {
            Messages.addFlashGlobalError("You must add at least 1 currency");
            result = false;
        }
        return result;
    }

    public boolean isCurrencyEnable(ProjectCurrencyEntity projectCurrency) {
        if (projectCurrency != null) {
            return StatusEnum.ENABLE.getId().intValue() == projectCurrency.getStatus().getId().intValue();
        } else {
            return true;
        }
    }

    public void addToProjectText() {
        log.info("add standard text to project");
        for (TextSnippetEntity ts : selectedGlobalTexts) {
            ProjectTextSnippetEntity entity = new ProjectTextSnippetEntity();
            entity.setId(tempProjectTextId);
            entity.setTextSnippet(ts);
            entity.setCode(ts.getCode());
            entity.setDescription(ts.getTextSnippet());
            entity.setLastUpdate(new Date());
            entity.setStatus(StatusEnum.ENABLE);
            projectTextSnippetList.add(entity);
            tempProjectTextId--;
        }
        globalStandardTextList.removeAll(selectedGlobalTexts);
        selectedGlobalTexts.clear();
    }

    public void removeFromProjectText() {
        log.info("remove standard text from project text");
        List<ProjectTextSnippetEntity> auxProjectTextSnippet = new ArrayList<>();
        for (ProjectTextSnippetEntity ts : selectedProjectTexts) {
            if(ts.getTextSnippet()!=null && ts.getTextSnippet().getId().longValue()>0L){
                globalStandardTextList.add(textSnippetService.findById(ts.getTextSnippet().getId()));
                if (ts.getId() > 0L) {
                    for (ProjectTextSnippetEntity pl : projectTextSnippetList) {
                        if (ts.getId().intValue() == pl.getId().intValue()) {
                            pl.setStatus(StatusEnum.DELETED);
                        }
                    }
                } else {
                    auxProjectTextSnippet.add(ts);
                }
            }else{
                TextSnippetEntity textSnippet = new TextSnippetEntity();
                //textSnippet.setId(temporaryTextId);
                textSnippet.setCode(ts.getCode());
                textSnippet.setTextSnippet(ts.getDescription());
                textSnippet.setProject(projectEntity);
                //globalStandardTextList.add(textSnippet);
                temporaryTextId--;
                if(ts.getId()>0){
                    for (ProjectTextSnippetEntity pe : projectTextSnippetList){
                        if(ts.getId().intValue() == pe.getId().intValue()){
                            pe.setStatus(StatusEnum.DELETED);
                        }
                    }
                }else {
                    auxProjectTextSnippet.add(ts);
                }
                textSnippetService.doSave(textSnippet);
                globalStandardTextList.clear();
                loadGlobalStandardTextList();
                removedStandardTextRepeat();
                ts.setStatus(StatusEnum.DELETED);
                projectTextSnippetService.doUpdate(ts);
            }
        }
        projectTextSnippetList.removeAll(auxProjectTextSnippet);
        selectedProjectTexts.clear();
    }

    public boolean isNewProjectEntityToSave() {
        return projectEntity != null && projectEntity.getId() == null ? true : false;
    }

    private void prepareProjectTextAndProjectDocumentToSave(){
        documentBean.prepareProjectDocListToSave();
        prepareToSaveProjectTextSnippet();
    }
    private void prepareToSaveProjectTextSnippet() {
        for (ProjectTextSnippetEntity p : projectTextSnippetList) {
            p.setId(null);
        }
    }

    public List<ProjectCurrencyEntity> filteredProjectCurrencies() {
        List<ProjectCurrencyEntity> list = new ArrayList<>();
        for (ProjectCurrencyEntity p : projectCurrencyList) {
            if (p.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()) {
                list.add(p);
            }
        }
        return list;
    }

    private void prepareToUpdateProjectTextSnippet() {
        for (ProjectTextSnippetEntity p : projectTextSnippetList) {
            if (p.getId() >= 1000) {
                p.setId(null);
            }
        }
    }

    private void loadSupplierProcList() {
        supplierProcList = supplierProcService.findAll();
    }

    private void loadCurrencyList() {
        currencyList = currencyService.findAll();
    }

    private void loadGlobalStandardTextList() {
        globalStandardTextList = textSnippetService.findGlobalAndByProject(projectId);
    }

    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    public void setProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public boolean isCreateProject() {
        return isCreateProject;
    }

    public void setCreateProject(boolean isCreateProject) {
        this.isCreateProject = isCreateProject;
    }

    public List<SupplierProcEntity> getSupplierProcList() {
        return supplierProcList;
    }

    public List<ProjectCurrencyEntity> getProjectCurrencyList() {
        return projectCurrencyList;
    }

    public ProjectCurrencyEntity getProjectCurrencyEntity() {
        return projectCurrencyEntity;
    }

    public void setProjectCurrencyEntity(ProjectCurrencyEntity projectCurrencyEntity) {
        this.projectCurrencyEntity = projectCurrencyEntity;
    }

    public List<CurrencyEntity> getCurrencyList() {
        return currencyList;
    }

    public Boolean getCreateCurrency() {
        return createCurrency;
    }

    public void setCreateCurrency(Boolean createCurrency) {
        this.createCurrency = createCurrency;
    }

    public List<TextSnippetEntity> getGlobalStandardTextList() {
        return globalStandardTextList;
    }

    public List<TextSnippetEntity> getSelectedGlobalTexts() {
        return selectedGlobalTexts;
    }

    public void setSelectedGlobalTexts(List<TextSnippetEntity> selectedGlobalTexts) {
        this.selectedGlobalTexts = selectedGlobalTexts;
    }


    @Inject
    private CurrencyBean currencyBean;

    public void addNewCurrency() {
        if (currencyBean.save()) {
            projectCurrencyEntity.setCurrency(currencyBean.getCurrency());
            loadCurrencyList();
        }
    }

    @Inject
    private TextSnippetBean standartText;

    public void addNewCustomText() {
        log.info("addNewCustomText");
        if (standartText.addProject(true)) {
            TextSnippetEntity ts = standartText.getTextSnippet();
            /*ts.setId(temporaryTextId);
            temporaryTextId--;*/
            ProjectTextSnippetEntity entity = new ProjectTextSnippetEntity();
            entity.setId(tempProjectTextId);
            entity.setTextSnippet(null);
            entity.setCode(ts.getCode());
            entity.setDescription(ts.getTextSnippet());
            entity.setLastUpdate(new Date());
            entity.setStatus(StatusEnum.ENABLE);
            entity.setProject(projectEntity);
            projectTextSnippetList.add(entity);
            tempProjectTextId--;
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('textSnippetModal').hide();");
            standartText.clear();
            projectTextSnippetService.doSaveNewProjectText(entity);
        }
        log.info("end..");
    }

    @Inject
    private PoTextBean poTextBean;

    public void saveNewCustomText(Long projectId) {
        log.info("saveNewCustomText");
        ProjectEntity project = projectService.findProjectById(projectId);
        if (project != null && standartText.addProject(false)) {
            TextSnippetEntity textSnippet = standartText.getTextSnippet();
            ProjectTextSnippetEntity ptse = projectService.addNewTextSnippet(project, textSnippet);
            projectTextSnippetListFromPO.add(ptse);
            log.info("projectTextSnippetListFromPO size: " + projectTextSnippetListFromPO.size());
            ClausesEntity clausesEntity = projectService.addNewClausesSnippet(ptse);
            clausesEntity.setId(tempClausesId);
            tempClausesId--;
            poTextBean.getDroppedTextSnippetList().add(clausesEntity);
            poTextBean.reorderDroppedTextSnippetList();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('textSnippetModal1').hide();");
        }
        log.info("end..");
    }

    public void updateDefaultStatusCurrency(ProjectCurrencyEntity currencyEntity) {
        log.info("updateDefaultStatusCurrency " + currencyEntity.getId());
        for (ProjectCurrencyEntity pce : projectCurrencyList) {
            pce.setProjectDefault(false);
            if (pce.getId().longValue() == currencyEntity.getId().longValue()) {
                pce.setProjectDefault(true);
            }
        }
    }

    public void editItem(ProjectTextSnippetEntity entity) {
        log.info("edit item");
        entity.startEditing();
        entity.storeOldValue(entity);
    }

    public void confirmItem(ProjectTextSnippetEntity entity) {
        log.info("confirm text");
        int index = projectTextSnippetList.indexOf(entity);
        projectTextSnippetList.set(index, entity);
        entity.stopEditing();
    }

    public void cancelEditionItem(ProjectTextSnippetEntity entity) {
        log.info("cancel item");
        if (!itemNoIsNotEmpty(entity)) {
            projectStandardTextList.remove(entity);
        } else {
            entity.stopEditing();
            entity = entity.getValueCloned();
        }
    }

    private boolean itemNoIsNotEmpty(ProjectTextSnippetEntity entity) {
        return StringUtils.isNotEmpty(entity.getDescription()) && StringUtils.isNotBlank(entity.getDescription());
    }

    public List<ProjectTextSnippetEntity> getProjectTextSnippetList() {
        return projectTextSnippetList;
    }

    public List<ProjectTextSnippetEntity> getSelectedProjectTexts() {
        return selectedProjectTexts;
    }

    public void setSelectedProjectTexts(List<ProjectTextSnippetEntity> selectedProjectTexts) {
        this.selectedProjectTexts = selectedProjectTexts;
    }

    public List<ProjectTextSnippetEntity> filteredList() {
        List<ProjectTextSnippetEntity> list = new ArrayList<>();
        for (ProjectTextSnippetEntity r : this.projectTextSnippetList) {
            if (r.getStatus() != null && r.getStatus().getId().intValue() == StatusEnum.ENABLE.getId().intValue()) {
                list.add(r);
            }
        }
        return list;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public void doSearchGlobalText() {
        globalStandardTextList.clear();
        globalStandardTextList = textSnippetService.findByText(searchTerm);
    }

    public List<ProjectTextSnippetEntity> getProjectTextSnippetListFromPO() {
        return projectTextSnippetListFromPO;
    }

    public boolean canDeleteTextSnippetCreatedOnProject(TextSnippetEntity entity){
        if(entity!=null && projectEntity!=null) {
            return textSnippetService.canDeleteTextSnippetFromProject(entity.getId(), projectEntity.getId());
        }else{
            return false;
        }
    }

    public void doDeleteTextSnippetCreatedOnProject(TextSnippetEntity entity){
        log.info("void doDeleteTextSnippetCreatedOnProject(TextSnippetEntity entity)");
        textSnippetService.doDelete(entity);
        globalStandardTextList.clear();
        loadGlobalStandardTextList();
        removedStandardTextRepeat();
    }
}

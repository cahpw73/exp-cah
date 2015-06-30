package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.currency.CurrencyService;
import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.procurement.boundary.currency.CurrencyBean;
import ch.swissbytes.procurement.boundary.logo.LogoBean;
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
public class ProjectBean implements Serializable {

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

    private List<SupplierProcEntity> supplierProcList;

    private List<LogoEntity> logoList;

    private List<ProjectCurrencyEntity> projectCurrencyList;

    private List<CurrencyEntity> currencyList;

    private List<TextSnippetEntity> globalStandardTextList;

    private List<TextSnippetEntity> selectedGlobalTexts;

    private List<TextSnippetEntity> projectStandardTextList;

    private List<ProjectTextSnippetEntity> projectTextSnippetList;

    private ProjectEntity projectEntity;

    private ProjectCurrencyEntity projectCurrencyEntity;

    private boolean isCreateProject;

    private Long projectId;

    private Boolean createCurrency = true;

    private Long temporalCurrencyId = -1L;

    private Long temporaryId=-1L;

    private final String DEFAULT_CURRENCY_FORMAT="#,###.00";


    @PostConstruct
    public void init (){
        log.info("ProjectBean created");
        projectEntity = new ProjectEntity();
        projectCurrencyEntity = new ProjectCurrencyEntity();
        projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
        supplierProcList = new ArrayList<>();
        projectCurrencyList = new ArrayList<>();
        logoList = new ArrayList<>();
        currencyList = new ArrayList<>();
        globalStandardTextList = new ArrayList<>();
        projectStandardTextList = new ArrayList<>();
        selectedGlobalTexts = new ArrayList<>();
        projectStandardTextList = new ArrayList<>();
        projectTextSnippetList = new ArrayList<>();
        loadSupplierProcList();
        loadLogoList();
        loadCurrencyList();

    }

    @PreDestroy
    public void destroy(){
        log.info("ProjectBean destroying");
    }

    public void loadActionCrud(){
        log.info("load action crud");
        if(isCreateProject){
            loadGlobalStandardTextList();
        }else{
            projectEntity = projectService.findProjectById(projectId);
            projectCurrencyList = projectService.findProjectCurrencyByProjectId(projectId);
            projectTextSnippetList = projectService.findProjectTextSnippetByProjectId(projectId);
            loadGlobalStandardTextList();
            loadAllStandardText();
        }
    }

    private void loadAllStandardText() {
        for(ProjectTextSnippetEntity pt : projectTextSnippetList){
            for(TextSnippetEntity ts : globalStandardTextList){
                if(pt.getTextSnippet().getId().intValue() == ts.getId().intValue()){
                    projectStandardTextList.add(ts);
                }
            }
        }
        globalStandardTextList.removeAll(projectStandardTextList);
    }

    public void doSave(){
        log.info("do save");
        if(dataValidate()) {
            prepareToSaveProjectTextSnippet();
            collectionAllData();
            projectEntity = projectService.doSave(projectEntity);
            Messages.addFlashGlobalInfo("The project " + projectEntity.getTitle() + " was store correctly", null);
        }
        mainMenuBean.select(0);
    }

    public void doUpdate(){
        log.info("do update");
        if(dataValidateToUpdate()) {
            prepareToUpdateProjectTextSnippet();
            collectionAllData();
            projectEntity =  projectService.doUpdate(projectEntity);
            Messages.addFlashGlobalInfo("The project "+projectEntity.getTitle()+" was update correctly",null);
        }
        mainMenuBean.select(0);
    }

    public String doSaveAndClose(){
        log.info("do save and close");
        log.info("ProjectEntity Id:" + (projectEntity.getId()!= null?projectEntity.getId():"null"));
        if(dataValidate()) {
            prepareToSaveProjectTextSnippet();
            collectionAllData();
            projectEntity =   projectService.doSave(projectEntity);
            Messages.addFlashGlobalInfo("The project "+projectEntity.getTitle()+" was store correctly",null);
            return "list?faces-redirect=true";
        }
        mainMenuBean.select(0);
        return "";
    }

    public String doUpdateAndClose(){
        log.info("do update and close");
        log.info("ProjectEntity Id:" + (projectEntity.getId()!= null?projectEntity.getId():"null"));
        if(dataValidateToUpdate()) {
            prepareToUpdateProjectTextSnippet();
            collectionAllData();
            projectEntity =  projectService.doUpdate(projectEntity);
            Messages.addFlashGlobalInfo("The project "+projectEntity.getTitle()+" was update correctly",null);
            return "list?faces-redirect=true";
        }
        mainMenuBean.select(0);
        return "";
    }

    private void collectionAllData() {
        log.info("collecting data");
        projectEntity.getCurrencies().addAll(projectCurrencyList);
        projectEntity.getProjectTextSnippetList().addAll(projectTextSnippetList);
        projectEntity.getGlobalStandardTextList().addAll(globalStandardTextList);
    }

    public void addCurrency(){
        log.info("add currency");
        if(validateCurrency()) {
            projectCurrencyEntity.setId(temporalId());
            projectCurrencyEntity.setProjectDefault(false);
            projectCurrencyEntity.setLastUpdate(new Date());
            projectCurrencyEntity.setStatus(StatusEnum.ENABLE);
            projectCurrencyList.add(projectCurrencyEntity);
            projectCurrencyEntity = new ProjectCurrencyEntity();
            projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
        }
    }

    private boolean validateCurrency(){
        boolean validated=true;
        if(StringUtils.isEmpty(projectCurrencyEntity.getFormat())&&StringUtils.isBlank(projectCurrencyEntity.getFormat())){
            Messages.addError("format","Please enter Format");
            validated=false;
        }
        if(projectCurrencyEntity.getCurrencyFactor()==null){
            Messages.addError("currencyFactor","Please enter Currency Factor");
            validated=false;
        }
        if(projectCurrencyEntity.getExchangeRate()==null){
            Messages.addError("exchangeRate","Please enter Exchange Rate");
            validated=false;
        }
        if(projectCurrencyEntity.getCurrency()==null){
            Messages.addError("currencyId","Please enter Currency");
            validated=false;
        }
        return validated;
    }

    private Long temporalId() {
        temporalCurrencyId = temporalCurrencyId - 1;
        return temporalCurrencyId;
    }

    public void updateCurrencyList(){
        log.info("update currency list");
        if(validateCurrency()) {
            projectCurrencyEntity.setLastUpdate(new Date());
            for (ProjectCurrencyEntity pc : projectCurrencyList) {
                if (pc.equals(projectCurrencyEntity)) {
                    pc = projectCurrencyEntity;
                }
            }
            projectCurrencyEntity = new ProjectCurrencyEntity();
            createCurrency = true;
            projectCurrencyEntity.setFormat(DEFAULT_CURRENCY_FORMAT);
        }
    }

    public void doDeleteCurrency(ProjectCurrencyEntity projectCurrency){
        int index=projectCurrencyList.indexOf(projectCurrency);
        if(index>=0){
            if(projectCurrencyList.get(index).getId()<0){
                projectCurrencyList.remove(index);
            }else{
                projectCurrencyList.get(index).setStatus(StatusEnum.DELETED);
                projectCurrencyList.get(index).setLastUpdate(new Date());
            }
        }
        projectCurrencyEntity = new ProjectCurrencyEntity();
        createCurrency = true;
    }

    public void doUpdateCurrency(ProjectCurrencyEntity projectCurrency){
        projectCurrencyEntity = projectCurrency;
        createCurrency = false;
    }

    private boolean dataValidate() {
        boolean result = true;
        if(projectService.existsProjectNumber(projectEntity.getProjectNumber())){
            Messages.addFlashError("projectNumber", "Project number was already registered!");
            result = false;
        }
        return result;
    }

    private boolean dataValidateToUpdate() {
        boolean result = true;
        if(projectService.validateDuplicityProjectNumber(projectEntity.getProjectNumber(), projectEntity.getId())){
            Messages.addError("projectNumber", "Project number was already registered!");
            result = false;
        }
        return result;
    }

    public boolean isCurrencyEnable(ProjectCurrencyEntity projectCurrency){
        if(projectCurrency != null) {
            return StatusEnum.ENABLE.getId().intValue() == projectCurrency.getStatus().getId().intValue();
        }else{
            return true;
        }
    }

    public void addToProjectText(){
        log.info("add standard text to project");
        for(TextSnippetEntity ts : selectedGlobalTexts){
            projectStandardTextList.add(ts);
        }
        globalStandardTextList.removeAll(selectedGlobalTexts);
        selectedGlobalTexts.clear();
    }

    public void removeFromProjectText(){
        log.info("remove standard text from project text");
        for(TextSnippetEntity ts : selectedGlobalTexts){
            globalStandardTextList.add(ts);
        }
        projectStandardTextList.removeAll(selectedGlobalTexts);
        selectedGlobalTexts.clear();
    }

    public boolean isNewProjectEntityToSave(){
        return projectEntity.getId() != null ? true : false;
    }

    private void prepareToSaveProjectTextSnippet() {
        for(TextSnippetEntity ts : projectStandardTextList){
            ProjectTextSnippetEntity projectTextSnippet = new ProjectTextSnippetEntity();
            projectTextSnippet.setLastUpdate(new Date());
            projectTextSnippet.setStatus(StatusEnum.ENABLE);
            projectTextSnippet.setTextSnippet(ts);
            projectTextSnippetList.add(projectTextSnippet);
        }
    }

    private void prepareToUpdateProjectTextSnippet() {
        for(ProjectTextSnippetEntity pt : projectTextSnippetList){
            boolean isTextDeleted = true;
            for(TextSnippetEntity ts : projectStandardTextList){
                if(pt.getId().intValue() == ts.getId().intValue()){
                    isTextDeleted = false;
                    break;
                }
            }
            if(isTextDeleted){
                pt.setLastUpdate(new Date());
                pt.setStatus(StatusEnum.DELETED);
            }
        }
        for(TextSnippetEntity ts : projectStandardTextList){
            ProjectTextSnippetEntity projectTextSnippet = new ProjectTextSnippetEntity();
            projectTextSnippet.setLastUpdate(new Date());
            projectTextSnippet.setStatus(StatusEnum.ENABLE);
            projectTextSnippet.setTextSnippet(ts);
            projectTextSnippetList.add(projectTextSnippet);
        }

    }

    private void loadSupplierProcList(){
        supplierProcList = supplierProcService.findAll();
    }

    private void loadLogoList(){
        logoList = logoService.findAll();
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

    public List<LogoEntity> getLogoList() {
        return logoList;
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

    public List<TextSnippetEntity> getProjectStandardTextList() {
        return projectStandardTextList;
    }

    public List<TextSnippetEntity> getSelectedGlobalTexts() {
        return selectedGlobalTexts;
    }

    public void setSelectedGlobalTexts(List<TextSnippetEntity> selectedGlobalTexts) {
        this.selectedGlobalTexts = selectedGlobalTexts;
    }

    @Inject
    private LogoBean logoBean;

    private Integer currentLogo;



   public void startReportLogo(){
       currentLogo=0;
       logoBean.restart();
   }
    public void startClientLogo(){
        currentLogo=1;
        logoBean.restart();
    }
    public void startClientFooter(){
        currentLogo=2;
        logoBean.restart();
    }
    public void startDefaultLogo(){
        currentLogo=3;
        logoBean.restart();
    }
    public void startDefaultFooter(){
        currentLogo=4;
        logoBean.restart();
    }

    public void saveLogo(){
        log.info("saving logo......");
        if(logoBean.saveForProject()) {
            loadLogoList();
            switch (currentLogo) {
                case 0://report logo
                    projectEntity.setReportLogo(logoBean.getLogo());
                    break;
                case 1: //client logo
                    projectEntity.setClientLogo(logoBean.getLogo());
                    break;
                case 2: //client footer
                    projectEntity.setClientFooter(logoBean.getLogo());
                    break;
                case 3://default logo
                    projectEntity.setDefaultLogo(logoBean.getLogo());
                    break;
                case 4:// default footer
                    projectEntity.setDefaultFooter(logoBean.getLogo());
                    break;

            }
        }
    }

    @Inject
    private CurrencyBean currencyBean;

    public void addNewCurrency(){
        if(currencyBean.save()) {
            projectCurrencyEntity.setCurrency(currencyBean.getCurrency());
            loadCurrencyList();
        }
    }

    @Inject
    private TextSnippetBean standartText;

    public void addNewCustomText(){
        log.info("addNewCustomText");
        if(standartText.addProject(true)) {
            TextSnippetEntity textSnippet=standartText.getTextSnippet();
            textSnippet.setId(temporaryId--);
            projectStandardTextList.add(textSnippet);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('textSnippetModal').hide();");
        }
        log.info("end..");
    }

    @Inject
    private PoTextBean poTextBean;

    public void saveNewCustomText(Long projectId){
        log.info("saveNewCustomText");
        ProjectEntity project=projectService.findProjectById(projectId);
        if(project!=null&&standartText.addProject(false)) {
            TextSnippetEntity textSnippet=standartText.getTextSnippet();
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('textSnippetModal1').hide();");
            ProjectTextSnippetEntity ptse=projectService.addNewTextSnippet(project, textSnippet);
            poTextBean.getDroppedTextSnippetList().add(ptse);
        }
        log.info("end..");
    }

    public void updateDefaultStatusCurrency(ProjectCurrencyEntity currencyEntity){
        for(ProjectCurrencyEntity pce:projectCurrencyList){
            if(currencyEntity.getId().longValue()!=pce.getId().longValue()){
                pce.setProjectDefault(false);
            }else{
                pce.setProjectDefault(true);
            }
        }
        for(ProjectCurrencyEntity pce:projectCurrencyList){
            log.info("status : " + pce.getProjectDefault());
        }
    }
}

package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.procurement.boundary.Bean;
import ch.swissbytes.procurement.util.FileUtil;
import ch.swissbytes.procurement.util.PdfDocumentFromHtmlServlet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
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

    private AttachmentMainDocumentEntity attachmentMainDocument;

    private ProjectDocumentEntity selectedProjectDoc;

    private MainDocumentEntity selectedMainDocument;

    private ProjectDocumentEntity projectDocument;

    private Long temporaryProjectDocId = -1L;

    private Long tempMainDocId = -1L;

    private String searchTerm;

    private ProjectEntity projectEntity;

    private Long projectEntityId;

    private boolean docPreview = false;

    private boolean documentEditing = false;

    private String tempFilePdfPath = "";


    @PostConstruct
    public void init() {
        log.info("DocumentBean created");
        mainDocumentList = new ArrayList<>();
        projectDocumentList = new ArrayList<>();
        selectedMainDocList = new ArrayList<>();
        selectedProjectDocList = new ArrayList<>();
        selectedProjectDoc = new ProjectDocumentEntity();
        attachmentMainDocument = new AttachmentMainDocumentEntity();
        projectDocument = new ProjectDocumentEntity();
    }

    @PreDestroy
    public void destroy() {
        log.info("DocumentBean destroying");
    }

    public void loadMainDocumentsEdit(final Long projectId) {
        if (projectDocumentList.isEmpty()) {
            projectDocumentList = projectDocumentService.findByProjectId(projectId);
        }
        mainDocumentList = mainDocumentService.findMainDocumentsToEdit(projectId);
        List<String> codesRepeat = new ArrayList<>();
        for (ProjectDocumentEntity pd : projectDocumentList) {
            for (MainDocumentEntity md : mainDocumentList) {
                if (StringUtils.equals(pd.getCode(), md.getCode())) {
                    codesRepeat.add(pd.getCode());
                    break;
                }
            }
        }
        List<MainDocumentEntity> mainDocumentAuxList = new ArrayList<>();
        for (MainDocumentEntity md : mainDocumentList) {
            for (String code : codesRepeat) {
                if (StringUtils.equals(md.getCode(), code)) {
                    mainDocumentAuxList.add(md);
                    break;
                }
            }
        }

        mainDocumentList.removeAll(mainDocumentAuxList);
    }

    public void loadMainDocumentsCreate() {
        mainDocumentList = mainDocumentService.findMainDocumentsToCreate();
    }

    public void addToProjectText() {
        log.info("add main doc template to project");
        for (MainDocumentEntity md : selectedMainDocList) {
            ProjectDocumentEntity entity = new ProjectDocumentEntity();
            entity.setId(temporaryProjectDocId);
            entity.setCode(md.getCode());
            entity.setDescription(md.getDescription());
            entity.setLastUpdate(new Date());
            entity.setMainDocumentEntity(md);
            entity.setStatus(StatusEnum.ENABLE);
            if (md.getAttachmentMainDocument() != null) {
                entity.setAttachmentProjectDocument(md.getAttachmentMainDocument());
            }
            projectDocumentList.add(entity);
            temporaryProjectDocId--;
        }
        mainDocumentList.removeAll(selectedMainDocList);
        selectedMainDocList.clear();
    }

    public void removeFromProjectText() {
        log.info("remove projectDocument from project");
        List<ProjectDocumentEntity> auxProjectList = new ArrayList<>();
        for (ProjectDocumentEntity pd : selectedProjectDocList) {
            if (pd.getMainDocumentEntity() != null) {
                mainDocumentList.add(mainDocumentService.findById(pd.getMainDocumentEntity().getId()));
                if (pd.getId() > 0) {
                    for (ProjectDocumentEntity pe : projectDocumentList) {
                        if (pd.getId().intValue() == pe.getId().intValue()) {
                            pe.setStatus(StatusEnum.DELETED);
                        }
                    }
                } else {
                    auxProjectList.add(pd);
                }
            } else {
                MainDocumentEntity mainDocument = mainDocumentService.findByProjectIdAndCode(pd.getProject().getId(), pd.getCode());
                if (mainDocument == null) {
                    mainDocument = new MainDocumentEntity();
                    mainDocument.setStatus(StatusEnum.ENABLE);
                    mainDocument.setId(tempMainDocId);
                    tempMainDocId--;
                    mainDocument.setDescription(pd.getDescription());
                    mainDocument.setCode(pd.getCode());
                    if (pd.getAttachmentProjectDocument() != null) {
                        mainDocument.setAttachmentMainDocument(pd.getAttachmentProjectDocument());
                    }
                    mainDocumentList.add(mainDocument);
                } else {
                    mainDocumentList.add(mainDocument);
                }

                if (pd.getId() > 0) {
                    for (ProjectDocumentEntity pe : projectDocumentList) {
                        if (pd.getId().intValue() == pe.getId().intValue()) {
                            pe.setStatus(StatusEnum.DELETED);
                        }
                    }
                } else {
                    auxProjectList.add(pd);
                }
            }
        }
        projectDocumentList.removeAll(auxProjectList);
        selectedProjectDocList.clear();
    }

    public void prepareProjectDocListToSave() {
        for (ProjectDocumentEntity p : projectDocumentList) {
            p.setId(null);
        }
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

    public void loadSeletedProjectDoc(ProjectDocumentEntity entity) {
        log.info("loadSeletedProjectDoc(ProjectDocumentEntity entity)");
        selectedProjectDoc = entity;
        docPreview = false;
    }

    public void createProjectDocPdfFile(){
        log.info("createProjectDocPdfFile()");
        String pathTempPdf = System.getProperty("fqmes.path.preview.pdf");
        String fileName = new Date().getTime()+""+selectedProjectDoc.getId();
        FileUtil fileUtil = new FileUtil();
        try {
            log.info("creating file on path = " + pathTempPdf);
            fileUtil.saveFileTemporal(selectedProjectDoc.getDescription(),pathTempPdf,fileName);
            tempFilePdfPath = pathTempPdf + File.separator + fileName;
            log.info("tempFilePdfPath = "  + tempFilePdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createProjectDocPdfFileToNewProjectDoc(){
        log.info("createProjectDocPdfFileToNewProjectDoc()");
        String pathTempPdf = System.getProperty("fqmes.path.preview.pdf");
        String fileName = new Date().getTime()+"";
        FileUtil fileUtil = new FileUtil();
        try {
            log.info("creating file on path = " + pathTempPdf);
            fileUtil.saveFileTemporal(projectDocument.getDescription(),pathTempPdf,fileName);
            tempFilePdfPath = pathTempPdf + File.separator + fileName;
            log.info("tempFilePdfPath = "  + tempFilePdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMainDocPdfFile(){
        log.info("createMainDocPdfFile()");
        String pathTempPdf = System.getProperty("fqmes.path.preview.pdf");
        String fileName = new Date().getTime()+""+selectedMainDocument.getId();
        FileUtil fileUtil = new FileUtil();
        try {
            log.info("creating file on path = " + pathTempPdf);
            fileUtil.saveFileTemporal(selectedMainDocument.getDescription(),pathTempPdf,fileName);
            tempFilePdfPath = pathTempPdf + File.separator + fileName;
            log.info("tempFilePdfPath = "  + tempFilePdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSelectedProjectDocumentToPdf(ProjectDocumentEntity entity){
        log.info("loadSelectedProjectDocumentToPdf(ProjectDocumentEntity entity)");
        loadSeletedProjectDoc(entity);
        createProjectDocPdfFile();
    }
    public void loadSelectedMainDocumentToPdf(MainDocumentEntity entity){
        log.info("loadSelectedProjectDocumentToPdf(ProjectDocumentEntity entity)");
        loadSelectedMainDocumentPreview(entity);
        createMainDocPdfFile();
    }

    public void loadSelectedProjectDocPreview(ProjectDocumentEntity entity) {
        selectedProjectDoc = entity;
        docPreview = true;
    }

    public void loadSelectedMainDocumentPreview(MainDocumentEntity entity) {
        selectedMainDocument = entity;
        docPreview = true;
    }

    public void updateProjectDocumentDt(String id) {
        for (ProjectDocumentEntity r : projectDocumentList) {
            if (r.getId().intValue() == selectedProjectDoc.getId().intValue()) {
                r.setDescription(selectedProjectDoc.getDescription());
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('projectDocModal" + id + "').hide();");
    }

    public void saveNewProjectDocumentWithProject() {
        projectDocument.setProject(projectEntity);
        projectDocument.setMainDocumentEntity(null);
        projectDocument.setCode(projectDocument.getCode().toUpperCase());
        projectDocumentService.doSaveNewProjectDocWithProject(projectDocument);
        //projectDocumentList = projectDocumentService.findByProjectId(projectEntityId);
        projectDocumentList.add(projectDocument);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addProjectDocModal').hide();");
    }

    public void saveNewProjectDocumentWithPdf() {
        projectDocument.setProject(projectEntity);
        projectDocument.setMainDocumentEntity(null);
        projectDocument.setCode(projectDocument.getCode().toUpperCase());
        projectDocumentService.doSaveWithPdf(projectDocument, attachmentMainDocument);
        //projectDocumentList = projectDocumentService.findByProjectId(projectEntityId);
        projectDocumentList.add(projectDocument);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addPdfModal').hide();");
        documentEditing = true;
    }

    public void resetProjectDocument() {
        projectDocument = new ProjectDocumentEntity();
        attachmentMainDocument = new AttachmentMainDocumentEntity();
    }

    public void doSearchGlobalText() {
        mainDocumentList.clear();
        mainDocumentList = mainDocumentService.findByText(searchTerm);
        List<MainDocumentEntity> auxMainDoc = new ArrayList<>();
        for (ProjectDocumentEntity pd : projectDocumentList) {
            for (MainDocumentEntity md : mainDocumentList) {
                if (pd.getCode().equals(md.getCode())) {
                    auxMainDoc.add(md);
                }
            }
        }
        mainDocumentList.removeAll(auxMainDoc);
    }

    public void handleUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        enterFile(uf);
    }

    public void enterFile(UploadedFile uf) {
        try {
            InputStream is = uf.getInputstream();
            attachmentMainDocument.setFile(IOUtils.toByteArray(is));
            attachmentMainDocument.setMimeType(uf.getContentType());
            attachmentMainDocument.setFileName(uf.getFileName());
            is.close();
        } catch (IOException ioe) {
            log.log(Level.SEVERE, String.format("problems with file [" + uf.getFileName() + "]"));
            log.log(Level.SEVERE, ioe.getMessage());
        } catch (Exception ex) {
            log.log(Level.SEVERE, String.format("problems with file [" + uf.getFileName() + "]"));
            log.log(Level.SEVERE, ex.getMessage());
        }
    }

    public boolean canDeleteMainDocumentCreatedFromProject(MainDocumentEntity entity) {
        List<MainDocumentEntity> list = mainDocumentService.findByMainDocIdAndProjectId(entity.getId(), projectEntityId);
        return list.isEmpty() ? false : true;
    }

    public void doDeleteMainDocCreatedFromProject(MainDocumentEntity entity){
        entity.setStatus(StatusEnum.DELETED);
        mainDocumentService.doUpdate(entity);
        filterMainDocFilter();
    }

    public void filterMainDocFilter(){
        List<MainDocumentEntity> auxList = new ArrayList<>();
        for(MainDocumentEntity md : mainDocumentList){
            if(md.getStatus().ordinal() == StatusEnum.DELETED.ordinal()){
                auxList.add(md);
            }
        }
        mainDocumentList.removeAll(auxList);
    }

    public void changeValueDocumentEditing() {
        documentEditing = false;
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

    public boolean isDocPreview() {
        return docPreview;
    }

    public void setDocPreview(boolean docPreview) {
        this.docPreview = docPreview;
    }

    public AttachmentMainDocumentEntity getAttachmentMainDocument() {
        return attachmentMainDocument;
    }

    public void setAttachmentMainDocument(AttachmentMainDocumentEntity attachmentMainDocument) {
        this.attachmentMainDocument = attachmentMainDocument;
    }

    public boolean isDocumentEditing() {
        return documentEditing;
    }

    public void setDocumentEditing(boolean documentEditing) {
        this.documentEditing = documentEditing;
    }

    public MainDocumentEntity getSelectedMainDocument() {
        return selectedMainDocument;
    }

    public void setSelectedMainDocument(MainDocumentEntity selectedMainDocument) {
        this.selectedMainDocument = selectedMainDocument;
    }

    public String getTempFilePdfPath() {
        log.info("tempFilePdfPath = " + tempFilePdfPath);
        return tempFilePdfPath;
    }

    public void setTempFilePdfPath(String tempFilePdfPath) {
        this.tempFilePdfPath = tempFilePdfPath;
    }
}
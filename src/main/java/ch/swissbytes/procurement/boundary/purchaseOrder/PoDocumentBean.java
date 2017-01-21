package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.mainDocument.AttachmentMainDocumentService;
import ch.swissbytes.Service.business.poDocument.PODocumentService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.procurement.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
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
 * Created by Christian 03/02/2016
 */

@Named
@ViewScoped
public class PoDocumentBean implements Serializable {

    public static final Logger log = Logger.getLogger(PoDocumentBean.class.getName());

    @Inject
    private PODocumentService poDocumentService;

    @Inject
    private ProjectDocumentService projectDocumentService;

    @Inject
    private PurchaseOrderService poeService;

    @Inject
    private ProjectService projectService;

    @Inject
    private AttachmentMainDocumentService attachmentDocumentService;

    private List<ProjectDocumentEntity> projectDocumentList;

    private List<PODocumentEntity> droppedPODocumentList;

    private List<PODocumentEntity> selectedPODocumentList;

    private List<PODocumentEntity> poDocumentList;

    private List<ProjectDocumentEntity> projectDocumentListToPO;

    private PODocumentEntity selectedPODocument;

    private ProjectDocumentEntity selectedProjectDocument;

    private PODocumentEntity poDocumentEntity;

    private AttachmentMainDocumentEntity attachmentMainDocument;

    private Long tempPODocumentId = -1L;

    private Long tempProjectDocumentId = -1l;

    private Long poId;

    private Long purchaseOrderId;

    private Long projectId;

    private boolean docPreview = false;

    private boolean documentEditing = false;

    private String tempFilePdfPath = "";

    @PostConstruct
    public void create() {
        log.info("create poTextBean");
        projectDocumentList = new ArrayList<>();
        droppedPODocumentList = new ArrayList<>();
        selectedPODocumentList = new ArrayList<>();
        poDocumentList = new ArrayList<>();
        projectDocumentListToPO = new ArrayList<>();
        attachmentMainDocument = new AttachmentMainDocumentEntity();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy poTextBean");
    }

    public void loadProjectDocuments(final PurchaseOrderEntity po, final Long projectId) {
        log.info("load project documents to main list");
        this.purchaseOrderId=po.getId();
        this.poId = po.getPurchaseOrderProcurementEntity().getId();
        this.projectId = projectId;
        projectDocumentList = projectDocumentService.findByProjectIdToEdit(projectId, po.getId());
        poDocumentList = poDocumentService.findByPOId(poId);
        droppedPODocumentList = poDocumentList;
        verifyExistScheduleToPOEdit();
        filteredProjectDocumentList();
    }

    private void verifyExistScheduleToPOEdit() {
        boolean existSchedule = false;
        for (PODocumentEntity pd : droppedPODocumentList) {
            if (pd.getScheduleE() != null && pd.getScheduleE() == true) {
                existSchedule = true;
                break;
            }
        }
        if (!existSchedule) {
            droppedPODocumentList.add(createEntity());
        }
        reorderDroppedPODocumentList();
    }

    public void loadTextNewPO(final Long projectId) {
        this.projectId = projectId;
        projectDocumentList = projectDocumentService.findByProjectIdToCreate(projectId);
        droppedPODocumentList.add(createEntity());
    }

    private PODocumentEntity createEntity() {
        PODocumentEntity entity = new PODocumentEntity();
        entity.setId(tempPODocumentId);
        entity.setDescription("<h4>Schedule E</h4>");
        entity.setCode("scheduleE");
        entity.setStatus(StatusEnum.ENABLE);
        entity.setLastUpdate(new Date());
        entity.setScheduleE(true);
        tempPODocumentId--;
        return entity;
    }

    private void filteredProjectDocumentList() {
        List<ProjectDocumentEntity> auxProjectDocList = new ArrayList<>();
        for (PODocumentEntity pd : droppedPODocumentList) {
            if (pd.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()) {
                if (pd.getProjectDocumentEntity() != null) {
                    ProjectDocumentEntity pe = projectDocumentService.findById(pd.getProjectDocumentEntity().getId());
                    auxProjectDocList.add(pe);
                }
            }
        }
        projectDocumentList.removeAll(auxProjectDocList);
    }

    public List<PODocumentEntity> filteredList() {
        List<PODocumentEntity> auxList = new ArrayList<>();
        for (PODocumentEntity p : droppedPODocumentList) {
            if (p.getStatus() != null && p.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()) {
                auxList.add(p);
            }
        }
        return auxList;
    }


    public void onStandardTextDrop(DragDropEvent ddEvent) {
        ProjectDocumentEntity projDoc = ((ProjectDocumentEntity) ddEvent.getData());
        PODocumentEntity poDoc = createPODocumentEntity(projDoc);
        droppedPODocumentList.add(poDoc);
        reorderDroppedPODocumentList();
        projectDocumentList.remove(projDoc);
    }

    public void copyToPODocument(ProjectDocumentEntity projDoc) {
        PODocumentEntity poDoc = createPODocumentEntity(projDoc);
        droppedPODocumentList.add(poDoc);
        reorderDroppedPODocumentList();
        projectDocumentList.remove(projDoc);
    }

    private PODocumentEntity createPODocumentEntity(ProjectDocumentEntity projDoc) {
        PODocumentEntity entity = new PODocumentEntity();
        entity.setId(tempPODocumentId);
        entity.setDescription(projDoc.getDescription());
        entity.setCode(projDoc.getCode());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setLastUpdate(new Date());
        entity.setProjectDocumentEntity(projDoc);
        if (projDoc.getAttachmentProjectDocument() != null) {
            entity.setAttachmentProjectDocument(projDoc.getAttachmentProjectDocument());
        }
        tempPODocumentId--;
        return entity;
    }

    public void onRowReorder(ReorderEvent event) {
        log.info("on row reorder");
        reorderDroppedPODocumentList();
    }

    public void removePODocuments() {
        List<PODocumentEntity> auxPODocList = new ArrayList<>();
        for (PODocumentEntity p : selectedPODocumentList) {
            if (p.getScheduleE() == null) {
                if (p.getProjectDocumentEntity() != null) {
                    projectDocumentList.add(projectDocumentService.findById(p.getProjectDocumentEntity().getId()));
                    if (p.getId() > 0) {
                        for (PODocumentEntity pe : droppedPODocumentList) {
                            if (p.getId().intValue() == pe.getId().intValue()) {
                                pe.setStatus(StatusEnum.DELETED);
                            }
                        }
                    } else {
                        auxPODocList.add(p);
                    }
                } else {
                    ProjectDocumentEntity pjDocument = createProjectDoc(p);
                    if (p.getId() > 0) {
                        for (PODocumentEntity pe : droppedPODocumentList) {
                            if (p.getId().intValue() == pe.getId().intValue()) {
                                pe.setStatus(StatusEnum.DELETED);
                            }
                        }
                    } else {
                        auxPODocList.add(p);
                    }
                    projectDocumentList.add(pjDocument);
                }
            }
        }
        droppedPODocumentList.removeAll(auxPODocList);
        selectedPODocumentList.clear();
        reorderDroppedPODocumentList();
    }

    private ProjectDocumentEntity createProjectDoc(PODocumentEntity p) {
        ProjectDocumentEntity projectDocumentEntity = new ProjectDocumentEntity();
        projectDocumentEntity.setId(tempProjectDocumentId);
        projectDocumentEntity.setCode(p.getCode());
        projectDocumentEntity.setDescription(p.getDescription());
        projectDocumentEntity.setLastUpdate(new Date());
        projectDocumentEntity.setStatus(StatusEnum.ENABLE);
        ProjectEntity projectEntity = projectService.findById(projectId);
        projectDocumentEntity.setProject(projectEntity);
        tempProjectDocumentId--;
        return projectDocumentEntity;
    }

    private void reorderDroppedPODocumentList() {
        int index = 1;
        for (PODocumentEntity p : droppedPODocumentList) {
            if (p.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()) {
                p.setNumberPODoc(index + ".0");
                index++;
            }
        }
    }

    public boolean hasStatusEnable(PODocumentEntity entity) {
        if (entity != null && entity.getStatus() != null) {
            return StatusEnum.ENABLE.ordinal() == entity.getStatus().ordinal();
        }
        return true;
    }

    public void loadSelectedPODocument(PODocumentEntity entity) {
        selectedPODocument = entity;
        docPreview = false;
    }

    public void loadSelectedPODocumentToPreview(PODocumentEntity entity) {
        selectedPODocument = entity;
        docPreview = true;
    }

    public void loadSelectedProjectDocumentToPreview(ProjectDocumentEntity entity) {
        selectedProjectDocument = entity;
        docPreview = true;
    }

    public void updatePODocumentDt(String id) {
        for (PODocumentEntity r : poDocumentList) {
            if (r.getId().intValue() == selectedPODocument.getId().intValue()) {
                r.setDescription(selectedPODocument.getDescription());
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('poDocModal" + id + "').hide();");
    }

    public void resetPODocument() {
        poDocumentEntity = new PODocumentEntity();
        attachmentMainDocument = new AttachmentMainDocumentEntity();
    }

    public void saveNewPODocument() {
        log.info("saving new po document");
        PurchaseOrderProcurementEntity poe = poeService.findPOEById(poId);
        poDocumentEntity.setId(null);
        poDocumentEntity.setStatus(StatusEnum.ENABLE);
        poDocumentEntity.setLastUpdate(new Date());
        poDocumentEntity.setPoProcurementEntity(poe);
        poDocumentEntity.setCode(poDocumentEntity.getCode().toUpperCase());
        droppedPODocumentList.add(poDocumentEntity);
        reorderDroppedPODocumentList();
        int order = 0;
        for (PODocumentEntity ps : droppedPODocumentList) {
            ps.setOrdered(order);
            order++;
        }
        ProjectDocumentEntity projDocEntity = doSaveNewProjectDocument(poDocumentEntity);
        poDocumentEntity.setProjectDocumentEntity(projDocEntity);
        poDocumentService.doSaveNewPODocumentDlg(poDocumentEntity);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addPODocModal').hide();");
    }

    public void saveNewPODocumentWithPdf() {
        PurchaseOrderProcurementEntity poe = poeService.findPOEById(poId);
        poDocumentEntity.setId(null);
        poDocumentEntity.setStatus(StatusEnum.ENABLE);
        poDocumentEntity.setLastUpdate(new Date());
        poDocumentEntity.setPoProcurementEntity(poe);
        poDocumentEntity.setCode(poDocumentEntity.getCode().toUpperCase());
        droppedPODocumentList.add(poDocumentEntity);
        reorderDroppedPODocumentList();
        int order = 0;
        for (PODocumentEntity ps : droppedPODocumentList) {
            ps.setOrdered(order);
            order++;
        }
        attachmentDocumentService.doSave(attachmentMainDocument);
        poDocumentEntity.setAttachmentProjectDocument(attachmentMainDocument);
        ProjectDocumentEntity projDocEntity = doSaveNewProjectDocument(poDocumentEntity);
        poDocumentEntity.setProjectDocumentEntity(projDocEntity);
        poDocumentService.doSaveNewPODocumentWithPdf(poDocumentEntity);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addPdfModal').hide();");
        documentEditing = true;
    }

    private ProjectDocumentEntity doSaveNewProjectDocument(PODocumentEntity poDocumentEntity) {
        ProjectEntity projectEntity = projectService.findProjectById(projectId);
        ProjectDocumentEntity projDocEntity = new ProjectDocumentEntity();
        projDocEntity.setDescription(poDocumentEntity.getDescription());
        projDocEntity.setCode(poDocumentEntity.getCode());
        projDocEntity.setStatus(StatusEnum.ENABLE);
        projDocEntity.setLastUpdate(new Date());
        projDocEntity.setProject(projectEntity);
        PurchaseOrderEntity poEntity = poeService.findByPOProcurementId(poDocumentEntity.getPoProcurementEntity().getId());
        projDocEntity.setPurchaseOrder(poEntity);
        if (poDocumentEntity.getAttachmentProjectDocument() != null) {
            projDocEntity.setAttachmentProjectDocument(poDocumentEntity.getAttachmentProjectDocument());
        }
        projectDocumentService.doSave(projDocEntity);
        projectDocumentListToPO.add(projDocEntity);
        return projDocEntity;
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

    public boolean verifyScheduleValue(PODocumentEntity entity) {
        return (entity != null && entity.getScheduleE() != null) ? true : false;
    }

    public boolean canDeleteProjectDocumentCreatedFromPOEdit(ProjectDocumentEntity entity) {
        List<ProjectDocumentEntity> list = projectDocumentService.findByProjectDocIdAndPoId(entity.getId(), purchaseOrderId);
        return list.isEmpty() ? false : true;
    }

    @Transactional
    public void doDeleteProjectDocumentCreatedFromPOEdit(ProjectDocumentEntity entity){
        entity.setStatus(StatusEnum.DELETED);
        projectDocumentService.doUpdate(entity);
        filterProjectDocumentDeleted();
    }

    private void filterProjectDocumentDeleted(){
        List<ProjectDocumentEntity> auxList = new ArrayList<>();
        for(ProjectDocumentEntity pd : projectDocumentList){
            if (pd.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()){
                auxList.add(pd);
            }
        }
        projectDocumentList.clear();
        projectDocumentList.addAll(auxList);
    }

    public void loadSelectedPODocumentToPdf(PODocumentEntity entity){
        log.info("loadSelectedPODocumentToPdf(PODocumentEntity entity)");
        loadSeletedPODoc(entity);
        createPODocPdfFile();
    }

    public void loadSeletedPODoc(PODocumentEntity entity) {
        log.info("loadSeletedPODoc(PODocumentEntity entity)");
        selectedPODocument = entity;
        docPreview = false;
    }

    public void createPODocPdfFile(){
        log.info("createPODocPdfFile()");
        String pathTempPdf = System.getProperty("fqmes.path.preview.pdf");
        String fileName = new Date().getTime()+"";
        FileUtil fileUtil = new FileUtil();
        try {
            log.info("creating file on path = " + pathTempPdf);
            fileUtil.saveFileTemporal(selectedPODocument.getDescription(),pathTempPdf,fileName);
            tempFilePdfPath = pathTempPdf + File.separator + fileName;
            log.info("tempFilePdfPath = "  + tempFilePdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeValueDocumentEditing() {
        documentEditing = false;
    }

    public List<ProjectDocumentEntity> getProjectDocumentList() {
        return projectDocumentList;
    }

    public List<PODocumentEntity> getDroppedPODocumentList() {
        return droppedPODocumentList;
    }

    public List<PODocumentEntity> getSelectedPODocumentList() {
        return selectedPODocumentList;
    }

    public void setSelectedPODocumentList(List<PODocumentEntity> selectedPODocumentList) {
        this.selectedPODocumentList = selectedPODocumentList;
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public boolean isDocPreview() {
        return docPreview;
    }

    public void setDocPreview(boolean docPreview) {
        this.docPreview = docPreview;
    }

    public List<ProjectDocumentEntity> getProjectDocumentListToPO() {
        return projectDocumentListToPO;
    }

    public AttachmentMainDocumentEntity getAttachmentMainDocument() {
        return attachmentMainDocument;
    }

    public void setAttachmentMainDocument(AttachmentMainDocumentEntity attachmentMainDocument) {
        this.attachmentMainDocument = attachmentMainDocument;
    }

    public ProjectDocumentEntity getSelectedProjectDocument() {
        return selectedProjectDocument;
    }

    public void setSelectedProjectDocument(ProjectDocumentEntity selectedProjectDocument) {
        this.selectedProjectDocument = selectedProjectDocument;
    }

    //************************************************************************

    public PODocumentEntity getSelectedPODocument() {
        return selectedPODocument;
    }

    public void setSelectedPODocument(PODocumentEntity selectedPODocument) {
        this.selectedPODocument = selectedPODocument;
    }

    public PODocumentEntity getPoDocumentEntity() {
        return poDocumentEntity;
    }

    public void setPoDocumentEntity(PODocumentEntity poDocumentEntity) {
        this.poDocumentEntity = poDocumentEntity;
    }

    public boolean isDocumentEditing() {
        return documentEditing;
    }

    public void setDocumentEditing(boolean documentEditing) {
        this.documentEditing = documentEditing;
    }

    public String getTempFilePdfPath() {
        return tempFilePdfPath;
    }

    public void setTempFilePdfPath(String tempFilePdfPath) {
        this.tempFilePdfPath = tempFilePdfPath;
    }
}

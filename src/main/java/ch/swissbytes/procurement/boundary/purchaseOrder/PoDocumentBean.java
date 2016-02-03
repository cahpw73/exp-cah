package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.poDocument.PODocumentService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.ReorderEvent;

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

    private List<ProjectDocumentEntity> projectDocumentList;

    private List<PODocumentEntity> droppedPODocumentList;

    private List<PODocumentEntity> selectedPODocumentList;

    private List<PODocumentEntity> poDocumentList;

    private Long tempPODocumentId = -1L;

    @PostConstruct
    public void create() {
        log.info("create poTextBean");
        projectDocumentList = new ArrayList<>();
        droppedPODocumentList = new ArrayList<>();
        selectedPODocumentList = new ArrayList<>();
        poDocumentList = new ArrayList<>();

        /*textEntity = new TextEntity();
        textSnippetList = new ArrayList<>();
        droppedTextSnippetList = new ArrayList<>();
        selectedClausesTextList = new ArrayList<>();
        clausesEntities = new ArrayList<>();*/
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy poTextBean");
    }

    public void copyToClauses(ProjectTextSnippetEntity text) {
        /*ClausesEntity clauses = createClausesEntity(text);
        droppedTextSnippetList.add(clauses);
        reorderDroppedTextSnippetList();
        textSnippetList.remove(text);*/
    }

    /*public void loadProjectTextSnippets(final Long projectId) {
        log.info("loading projectTextSnippet list");
        textSnippetList = projectTextSnippetService.findByProjectId(projectId);
    }*/

    /*public void loadText(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity, final Long projectId) {
        log.info("loadText");
        textEntity = textService.findByPoId(purchaseOrderProcurementEntity.getId());
        textSnippetList = projectTextSnippetService.findByProjectId(projectId);
        if (textEntity != null) {
            clausesEntities = textService.findClausesByTextId(textEntity.getId());

            droppedTextSnippetList = clausesEntities;
            List<ProjectTextSnippetEntity> listToRemove = new ArrayList<>();
            for (ClausesEntity ce : droppedTextSnippetList) {
                if (ce.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()) {
                    ProjectTextSnippetEntity pt = projectTextSnippetService.findById(ce.getProjectTextSnippet().getId());
                    listToRemove.add(pt);
                }
            }
            textSnippetList.removeAll(listToRemove);
        }
    }*/

    public void loadProjectDocuments(final Long poId, final Long projectId){
        log.info("load project documents to main list");
        projectDocumentList = projectDocumentService.findByProjectId(projectId);
        poDocumentList = poDocumentService.findByPOId(poId);
        droppedPODocumentList = poDocumentList;
        List<ProjectDocumentEntity> auxProjectDocList = new ArrayList<>();
        for(PODocumentEntity pd : droppedPODocumentList){
            if(pd.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()){
                ProjectDocumentEntity pe = projectDocumentService.findById(pd.getProjectDocumentEntity().getId());
                auxProjectDocList.add(pe);
            }
        }
        projectDocumentList.removeAll(auxProjectDocList);
    }

    /*public List<ClausesEntity> filteredList1() {
        List<ClausesEntity> list = new ArrayList<>();
        for (ClausesEntity r : this.droppedTextSnippetList) {
            if (r.getStatus() != null && r.getStatus().getId().intValue() == StatusEnum.ENABLE.getId().intValue()) {
                list.add(r);
            }
        }
        return list;
    }*/
    public List<PODocumentEntity> filteredList(){
        List<PODocumentEntity> auxList = new ArrayList<>();
        for(PODocumentEntity p : droppedPODocumentList){
          if(p.getStatus() != null && p.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()){
              auxList.add(p);
          }
        }
        return  auxList;
    }

    public void loadTextNewPO(final Long projectId) {
        //textSnippetList = projectTextSnippetService.findByProjectId(projectId);
        projectDocumentList = projectDocumentService.findByProjectId(projectId);
    }

    /*public void onStandardTextDrop(DragDropEvent ddEvent) {
        log.info("on standard text drop");
        ProjectTextSnippetEntity poText = ((ProjectTextSnippetEntity) ddEvent.getData());
        ClausesEntity clausesEntity = createClausesEntity(poText);
        droppedTextSnippetList.add(clausesEntity);
        reorderDroppedTextSnippetList();
        textSnippetList.remove(poText);
    }*/

    public void onStandardTextDrop(DragDropEvent ddEvent){
        ProjectDocumentEntity projDoc = ((ProjectDocumentEntity) ddEvent.getData());
        PODocumentEntity poDocumentEntity = createPODocumentEntity(projDoc);
        droppedPODocumentList.add(poDocumentEntity);
        reorderDroppedPODocumentList();
        projectDocumentList.remove(projDoc);
    }

    private PODocumentEntity createPODocumentEntity(ProjectDocumentEntity projDoc){
        PODocumentEntity entity = new PODocumentEntity();
        entity.setId(tempPODocumentId);
        entity.setDescription(projDoc.getDescription());
        entity.setCode(projDoc.getCode());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setLastUpdate(new Date());
        entity.setProjectDocumentEntity(projDoc);
        tempPODocumentId--;
        return entity;
    }

    /*private ClausesEntity createClausesEntity(ProjectTextSnippetEntity poText) {
        ClausesEntity entity = new ClausesEntity();
        entity.setId(tempClausesId);
        tempClausesId++;
        entity.setLastUpdate(new Date());
        entity.setClauses(poText.getDescription());
        entity.setCode(poText.getCode());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setProjectTextSnippet(poText);
        return entity;
    }*/

    public void onRowReorder(ReorderEvent event) {
        log.info("on row reorder");
        //reorderDroppedTextSnippetList();
        reorderDroppedPODocumentList();
    }

    public void removeClauses() {
        for (ClausesEntity ts : selectedClausesTextList) {
            textSnippetList.add(projectTextSnippetService.findById(ts.getProjectTextSnippet().getId()));
            if (ts.getId() > 0 && ts.getId() < 1000) {
                for (ClausesEntity pl : droppedTextSnippetList) {
                    if (ts.getId().intValue() == pl.getId().intValue()) {
                        pl.setStatus(StatusEnum.DELETED);
                    }
                }
            } else {
                droppedTextSnippetList.remove(ts);
            }
        }
        selectedClausesTextList.clear();
        reorderDroppedTextSnippetList();
    }

    public void removePODocuments(){
        for(PODocumentEntity p : selectedPODocumentList){
            projectDocumentList.add(projectDocumentService.findById(p.getProjectDocumentEntity().getId()));
            if(p.getId() > 0){
                for(PODocumentEntity pe : droppedPODocumentList){
                    if(p.getId().intValue() == pe.getId().intValue()){
                        pe.setStatus(StatusEnum.DELETED);
                    }
                }
            }else{
                droppedPODocumentList.remove(p);
            }
        }
        selectedPODocumentList.clear();
        reorderDroppedPODocumentList();
    }

    private void reorderDroppedTextSnippetList(){
        int index = 1;
        for(ClausesEntity c : droppedTextSnippetList){
            if(c.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()){
                c.setNumberClause(index+".0");
                index++;
            }
        }
    }

    private void reorderDroppedPODocumentList(){
        int index = 1;
        for(PODocumentEntity p : droppedPODocumentList){
            if(p.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()){
                p.setNumberPODoc(index+".0");
                index++;
            }
        }
    }

    public void editItem(ClausesEntity entity) {
        log.info("edit item");
        entity.startEditing();
        entity.storeOldValue(entity);
    }

    public void confirmItem(ClausesEntity entity) {
        log.info("confirm text");
        int index = droppedTextSnippetList.indexOf(entity);
        droppedTextSnippetList.set(index, entity);
        entity.stopEditing();
    }

    public void cancelEditionItem(ClausesEntity entity) {
        log.info("cancel item");
        if (!itemNoIsNotEmpty(entity)) {
            droppedTextSnippetList.remove(entity);
        } else {
            entity.stopEditing();
            entity = entity.getValueCloned();
        }
    }

    /*public boolean hasStatusEnable(ClausesEntity entity) {
        if (entity != null && entity.getStatus() != null) {
            return StatusEnum.ENABLE.ordinal() == entity.getStatus().ordinal();
        }
        return true;
    }*/

    public boolean hasStatusEnable(PODocumentEntity entity) {
        if(entity != null && entity.getStatus() != null){
            return StatusEnum.ENABLE.ordinal() == entity.getStatus().ordinal();
        }
        return true;
    }

    private boolean itemNoIsNotEmpty(ClausesEntity entity) {
        return StringUtils.isNotEmpty(entity.getClauses()) && StringUtils.isNotBlank(entity.getClauses());
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

    //************************************************************************

    public List<ProjectTextSnippetEntity> getTextSnippetList() {
        return textSnippetList;
    }

    public List<ClausesEntity> getDroppedTextSnippetList() {
        return droppedTextSnippetList;
    }

    public List<ClausesEntity> getSelectedClausesTextList() {
        return selectedClausesTextList;
    }

    public void setSelectedClausesTextList(List<ClausesEntity> selectedClausesTextList) {
        this.selectedClausesTextList = selectedClausesTextList;
    }

    public TextEntity getTextEntity() {
        return textEntity;
    }

    public void setTextEntity(TextEntity textEntity) {
        this.textEntity = textEntity;
    }

    @Inject
    private ProjectTextSnippetService projectTextSnippetService;

    @Inject
    private TextService textService;

    private List<ProjectTextSnippetEntity> textSnippetList;

    private List<ClausesEntity> droppedTextSnippetList;

    private List<ClausesEntity> selectedClausesTextList;

    private List<ClausesEntity> clausesEntities;

    private TextEntity textEntity;

    private Long tempClausesId = 1000L;

}

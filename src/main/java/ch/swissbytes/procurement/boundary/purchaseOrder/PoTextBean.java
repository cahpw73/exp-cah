package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.ClausesEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderProcurementEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.model.entities.TextEntity;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Christian 02/06/2015
 */

@Named
@ViewScoped
public class PoTextBean implements Serializable {

    public static final Logger log = Logger.getLogger(PoTextBean.class.getName());

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


    @PostConstruct
    public void create() {
        log.info("create poTextBean");
        textEntity = new TextEntity();
        textSnippetList = new ArrayList<>();
        droppedTextSnippetList = new ArrayList<>();
        selectedClausesTextList = new ArrayList<>();
        clausesEntities = new ArrayList<>();
    }

    public void copyToPreamble(String text) {
        textEntity.setPreamble(text);
    }

    public void copyToClauses(ProjectTextSnippetEntity text) {
        ClausesEntity clauses = createClausesEntity(text);
        droppedTextSnippetList.add(clauses);
        reorderDroppedTextSnippetList();
        textSnippetList.remove(text);
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy poTextBean");
    }

    public void loadProjectTextSnippets(final Long projectId) {
        log.info("loading projectTextSnippet list");
        textSnippetList = projectTextSnippetService.findByProjectId(projectId);
    }

    public void loadText(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity, final Long projectId) {
        //check this if we can improve. it takes about 1 second.
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
    }

    public void loadTextNewPO(final Long projectId) {
        textSnippetList = projectTextSnippetService.findByProjectId(projectId);
    }

    public void onStandardTextDrop(DragDropEvent ddEvent) {
        log.info("on standard text drop");
        ProjectTextSnippetEntity poText = ((ProjectTextSnippetEntity) ddEvent.getData());
        ClausesEntity clausesEntity = createClausesEntity(poText);
        droppedTextSnippetList.add(clausesEntity);
        reorderDroppedTextSnippetList();
        textSnippetList.remove(poText);
    }

    private ClausesEntity createClausesEntity(ProjectTextSnippetEntity poText) {
        ClausesEntity entity = new ClausesEntity();
        entity.setId(tempClausesId);
        tempClausesId++;
        entity.setLastUpdate(new Date());
        try {
            entity.setClauses(new String(poText.getDescription().getBytes(),"CP1252"));
            //entity.setIsEditable(true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setCode(poText.getCode());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setProjectTextSnippet(poText);
        return entity;
    }

    public void onRowReorder(ReorderEvent event) {
        log.info("on row reorder");
        reorderDroppedTextSnippetList();
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

    private void reorderDroppedTextSnippetList(){
        int index = 1;
        for(ClausesEntity c : droppedTextSnippetList){
            if(c.getStatus().ordinal() == StatusEnum.ENABLE.ordinal()){
                c.setNumberClause(index+".0");
                index++;
            }
        }
    }

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

    public boolean hasStatusEnable(ClausesEntity entity) {
        if (entity != null && entity.getStatus() != null) {
            return StatusEnum.ENABLE.ordinal() == entity.getStatus().ordinal();
        }
        return true;
    }

    private boolean itemNoIsNotEmpty(ClausesEntity entity) {
        return StringUtils.isNotEmpty(entity.getClauses()) && StringUtils.isNotBlank(entity.getClauses());
    }

    public List<ClausesEntity> filteredList() {
        log.info("filteredList()");
        for (ClausesEntity c : droppedTextSnippetList) {
            log.info("drooped text Id: " + c.getId() + " code: " + c.getCode());
        }
        List<ClausesEntity> list = new ArrayList<>();
        for (ClausesEntity r : this.droppedTextSnippetList) {
            if (r.getStatus() != null && r.getStatus().getId().intValue() == StatusEnum.ENABLE.getId().intValue()) {
                list.add(r);
            }
        }
        for (ClausesEntity c : list) {
            log.info("list text Id: " + c.getId() + " code: " + c.getCode());
        }
        log.info("list size: " + list.size());
        return list;
    }

}

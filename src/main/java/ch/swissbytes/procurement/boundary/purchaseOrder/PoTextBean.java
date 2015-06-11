package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.ClausesEntity;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.model.entities.TextEntity;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.ReorderEvent;

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

    private List<ProjectTextSnippetEntity> droppedTextSnippetList;

    private List<ProjectTextSnippetEntity> selectedClausesTextList;

    private List<ClausesEntity> clausesEntities;

    private TextEntity textEntity;


    @PostConstruct
    public void create() {
        log.info("create poTextBean");
        textEntity = new TextEntity();
        textSnippetList = new ArrayList<>();
        droppedTextSnippetList = new ArrayList<>();
        selectedClausesTextList = new ArrayList<>();
        clausesEntities = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy poTextBean");
    }

    public void loadProjectTextSnippets(final Long projectId){
        log.info("loading projectTextSnippet list");
        textSnippetList = projectTextSnippetService.findByProjectId(projectId);
    }

    public void loadText(POEntity poEntity, final Long projectId) {
        log.info("loadText");
        textEntity = textService.findByPoId(poEntity.getId());
        clausesEntities = textService.findClausesByTextId(textEntity.getId());
        textSnippetList = projectTextSnippetService.findByProjectId(projectId);
        droppedTextSnippetList = projectTextSnippetService.findTextSnippetByClausesId(clausesEntities);
        List<ProjectTextSnippetEntity> listToRemove = new ArrayList<>();
        for(ProjectTextSnippetEntity ps : droppedTextSnippetList){
            for (ProjectTextSnippetEntity t : textSnippetList){
                if(t.getId().intValue() == ps.getId().intValue()){
                    listToRemove.add(t);
                }
            }
        }
        textSnippetList.removeAll(listToRemove);
    }

    public void onStandardTextDrop(DragDropEvent ddEvent) {
        log.info("on standard text drop");
        ProjectTextSnippetEntity poText = ((ProjectTextSnippetEntity) ddEvent.getData());
        droppedTextSnippetList.add(poText);
        textSnippetList.remove(poText);
    }

    public void onRowReorder(ReorderEvent event) {
        log.info("on row reorder");
        for(ProjectTextSnippetEntity p : textSnippetList){
            log.info("text Id: " + p.getId());
        }
    }

    public void removeClauses(){
        droppedTextSnippetList.removeAll(selectedClausesTextList);
        textSnippetList.addAll(selectedClausesTextList);
        selectedClausesTextList.clear();
    }

    public List<ProjectTextSnippetEntity> getTextSnippetList() {
        return textSnippetList;
    }

    public List<ProjectTextSnippetEntity> getDroppedTextSnippetList() {
        return droppedTextSnippetList;
    }

    public List<ProjectTextSnippetEntity> getSelectedClausesTextList() {
        return selectedClausesTextList;
    }

    public void setSelectedClausesTextList(List<ProjectTextSnippetEntity> selectedClausesTextList) {
        this.selectedClausesTextList = selectedClausesTextList;
    }

    public TextEntity getTextEntity() {
        return textEntity;
    }

    public void setTextEntity(TextEntity textEntity) {
        this.textEntity = textEntity;
    }


}

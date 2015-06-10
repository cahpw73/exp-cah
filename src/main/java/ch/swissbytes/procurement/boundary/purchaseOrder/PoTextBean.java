package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
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

    private List<ProjectTextSnippetEntity> textSnippetList;

    private List<ProjectTextSnippetEntity> droppedTextSnippetList;

    private List<ProjectTextSnippetEntity> selectedClausesTextList;

    @PostConstruct
    public void create() {
        log.info("create poTextBean");
        textSnippetList = new ArrayList<>();
        droppedTextSnippetList = new ArrayList<>();
        selectedClausesTextList = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy poTextBean");
    }

    public void loadProjectTextSnippets(final Long projectId){
        log.info("loading projectTextSnippet list");
        textSnippetList = projectTextSnippetService.findByProjectId(projectId);
    }

    public void onStandardTextDrop(DragDropEvent ddEvent) {
        log.info("on standard text drop");
        ProjectTextSnippetEntity poText = ((ProjectTextSnippetEntity) ddEvent.getData());
        droppedTextSnippetList.add(poText);
        textSnippetList.remove(poText);
    }

    public void onRowReorder(ReorderEvent event) {
        log.info("on row reorder");
        //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Row Moved", "From: " + event.getFromIndex() + ", To:" + event.getToIndex());
        //FacesContext.getCurrentInstance().addMessage(null, msg);
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
}

package ch.swissbytes.procurement.boundary.mainDocument;


import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;
import ch.swissbytes.domain.types.ModeOperationEnum;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class MainDocumentBean implements Serializable {

    private static final Logger log = Logger.getLogger(MainDocumentBean.class.getName());

    @Inject
    private MainDocumentService service;
    private List<MainDocumentEntity> list;
    private MainDocumentEntity mainDocument;
    private MainDocumentEntity selected;
    private ModeOperationEnum mode;
    private String criteria;

    @PostConstruct
    public void create() {
        log.info("creating currency bean");
        loadList();
        mode = ModeOperationEnum.NEW;
        mainDocument = new MainDocumentEntity();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying currency bean");
    }

    public void edit(Long currencyId) {
        mainDocument = service.findById(currencyId);
        mode = ModeOperationEnum.UPDATE;
    }

    public String doSave() {
        if (!validate(mainDocument)) {
            return "";
        }
        service.doSave(mainDocument);
        return "main-document?faces-redirect=true";
    }

    public void clear(){
        mainDocument=new MainDocumentEntity();
    }


    private boolean validate(MainDocumentEntity entity) {
        boolean valid = true;
        if (service.isCodeDuplicated(entity.getId(), entity.getCode())) {
            Messages.addFlashError("codeTS", String.format("Code [%s] is duplicated ", entity.getCode()));
            valid = false;
        }
        return valid;
    }

    public boolean addProject(boolean hideModal){
        if(!validate(mainDocument)){
            return false;
        }
        if(hideModal) {
            RequestContext context = RequestContext.getCurrentInstance();
            RequestContext.getCurrentInstance().update("pickSystemFormId");
            context.execute("PF('textSnippetModal').hide();");
        }
        return true;
    }

    public String doUpdate() {
        if (!validate(selected)) {
            return "";
        }
        selected.setLastUpdate(new Date());
        selected.setCode(selected.getCode().toUpperCase());
        service.doUpdate(selected);
        return "main-document?faces-redirect=true";
    }

    public String doDelete() {
        service.delete(selected);
        return "main-document?faces-redirect=true";
    }

    public void search() {
        log.info("criteria : " + criteria);
        list.clear();
        list = service.findByText(criteria);
        if (list.size() == 1) {
            selected = list.get(0);
        }

    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    private void loadList() {
        list = service.getMainDocumentList();
    }

    public List<MainDocumentEntity> getList() {
        return list;
    }

    public MainDocumentEntity getMainDocument() {
        return mainDocument;
    }

    public void setMainDocument(MainDocumentEntity mainDocument) {
        this.mainDocument = mainDocument;
    }

    public MainDocumentEntity getSelected() {
        return selected;
    }

    public void setSelected(MainDocumentEntity selected) {
        this.selected = selected;
    }
}

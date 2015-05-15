package ch.swissbytes.procurement.boundary.textSnippet;


import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;
import ch.swissbytes.domain.types.ModeOperationEnum;
import ch.swissbytes.domain.types.StatusEnum;
import org.omnifaces.util.Messages;

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
public class TextSnippetBean implements Serializable {

    private static final Logger log = Logger.getLogger(TextSnippetBean.class.getName());

    @Inject
    private TextSnippetService service;
    private List<TextSnippetEntity> list;
    private TextSnippetEntity textSnippet;
    private TextSnippetEntity selected;
    private ModeOperationEnum mode;
    private String criteria;

    @PostConstruct
    public void create() {
        log.info("creating currency bean");
        loadList();
        mode = ModeOperationEnum.NEW;
        textSnippet = new TextSnippetEntity();
        textSnippet.setStatus(StatusEnum.ENABLE);
    }

    public void edit(Long currencyId) {
        textSnippet = service.findById(currencyId);
        mode = ModeOperationEnum.UPDATE;
    }

    public String doSave() {
        if (!validate(textSnippet)) {
            return "";
        }

        textSnippet.setLastUpdate(new Date());
        service.doSave(textSnippet);
        return "textSnippet?faces-redirect=true";
    }

    private boolean validate(TextSnippetEntity textSnippet) {
        boolean valid = true;
        if (service.isCodeDuplicated(textSnippet.getId(), textSnippet.getCode())) {
            Messages.addFlashError("currencyCode", String.format("Code [%s] is duplicated ", textSnippet.getCode()));
            valid = false;
        }
        return valid;
    }

    public String doUpdate() {
        if (!validate(selected)) {
            return "";
        }
        textSnippet.setLastUpdate(new Date());
        service.doUpdate(textSnippet);
        return "textSnippet?faces-redirect=true";
    }

    public String doDelete(Long id) {
        textSnippet = service.findById(id);
        service.delete(textSnippet);
        return "textSnippet?faces-redirect=true";
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
        list = service.getTextSnippetList();
    }

    public List<TextSnippetEntity> getList() {
        return list;
    }

    public TextSnippetEntity getTextSnippet() {
        return textSnippet;
    }

    public void setTextSnippet(TextSnippetEntity currency) {
        this.textSnippet = currency;
    }

    public TextSnippetEntity getSelected() {
        return selected;
    }

    public void setSelected(TextSnippetEntity selected) {
        log.info("seting selected " + selected);
        this.selected = selected;
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying currency bean");
    }

}

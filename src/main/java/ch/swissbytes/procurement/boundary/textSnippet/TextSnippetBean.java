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
    private TextSnippetEntity textSnippetEntity;
    private ModeOperationEnum mode;

    @PostConstruct
    public void create() {
        log.info("creating currency bean");
        loadList();
        mode = ModeOperationEnum.NEW;
        textSnippetEntity = new TextSnippetEntity();
        textSnippetEntity.setStatus(StatusEnum.ENABLE);
    }

    public void edit(Long currencyId) {
        textSnippetEntity = service.findById(currencyId);
        mode = ModeOperationEnum.UPDATE;
    }

    public String doSave() {
        if (!validate()) {
            return "";
        }

        textSnippetEntity.setLastUpdate(new Date());
        service.doSave(textSnippetEntity);
        return "currency?faces-redirect=true";
    }

    private boolean validate() {
        boolean valid = true;
        if (service.isCodeDuplicated(textSnippetEntity.getId(), textSnippetEntity.getCode())) {
            Messages.addFlashError("currencyCode", String.format("Code [%s] is duplicated ", textSnippetEntity.getCode()));
            valid = false;
        }


        return valid;
    }

    public String doUpdate() {
        if (!validate()) {
            return "";
        }
        textSnippetEntity.setLastUpdate(new Date());
        service.doUpdate(textSnippetEntity);
        return "currency?faces-redirect=true";
    }

    public String doDelete(Long id) {
        textSnippetEntity = service.findById(id);
        service.delete(textSnippetEntity);
        return "currency?faces-redirect=true";
    }

    private void loadList() {
        list = service.getTextSnippetList();
    }

    public List<TextSnippetEntity> getList() {
        return list;
    }

    public TextSnippetEntity getTextSnippetEntity() {
        return textSnippetEntity;
    }

    public void setTextSnippetEntity(TextSnippetEntity currency) {
        this.textSnippetEntity = currency;
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying currency bean");
    }

}

package ch.swissbytes.Service.business.textSnippet;


import ch.swissbytes.Service.business.Service;

import ch.swissbytes.domain.model.entities.TextSnippetEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class TextSnippetService extends Service<TextSnippetEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(TextSnippetService.class.getName());

    @Inject
    private TextSnippetDao dao;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    @Transactional
    public void delete(TextSnippetEntity currency) {
        if(canDeleteTextSnippet(currency)) {
            currency.setStatus(StatusEnum.DELETED);
            currency.setLastUpdate(new Date());
            dao.update(currency);
        }else{
            Messages.addFlashGlobalError("Can not delete " + currency.getCode() + " because it is already being used");
        }
    }

    @Transactional
    public void doSave(TextSnippetEntity textSnippet) {
        textSnippet.setLastUpdate(new Date());
        textSnippet.setStatus(StatusEnum.ENABLE);
        textSnippet.setCode(textSnippet.getCode().toUpperCase());
        super.doSave(textSnippet);
    }

    @Override
    public TextSnippetEntity save(TextSnippetEntity textSnippetEntity) {
        textSnippetEntity.setLastUpdate(new Date());
        textSnippetEntity.setStatus(StatusEnum.ENABLE);
        return super.save(textSnippetEntity);
    }

    @Transactional
    public TextSnippetEntity findById(Long id) {
        List<TextSnippetEntity> list = dao.findById(TextSnippetEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Transactional
    public List<TextSnippetEntity> getTextSnippetList() {
        log.info("getTextSnippetList");
        return dao.getTextSnippetList();
    }

    @Transactional
    public boolean isCodeDuplicated(Long id, String code) {
        return !dao.findByCodeButWithNoId(code, id).isEmpty();
    }

    @Transactional
    public List<TextSnippetEntity> findByText(final String text) {
        return dao.findByText(text);
    }

    @Transactional
    public List findGlobalAndByProject(Long id) {
        return dao.findGlobalAndByProject(id);
    }

    public boolean canDeleteTextSnippet(TextSnippetEntity entity) {
        return dao.findProjectTextSnippetByTextSnippetId(entity.getId()).isEmpty() ? true : false;
    }
}

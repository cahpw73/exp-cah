package ch.swissbytes.Service.business.mainDocument;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 27/01/16.
 */
public class MainDocumentService extends Service<TextSnippetEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(MainDocumentService.class.getName());

    @Inject
    private MainDocumentDao dao;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    @Transactional
    public void delete(TextSnippetEntity currency) {
        currency.setStatus(StatusEnum.DELETED);
        currency.setLastUpdate(new Date());
        dao.update(currency);
    }
    @Transactional
    public void doSave(TextSnippetEntity textSnippet){
        textSnippet.setLastUpdate(new Date());
        textSnippet.setStatus(StatusEnum.ENABLE);
        textSnippet.setCode(textSnippet.getCode().toUpperCase());
        super.doSave(textSnippet);
    }

    @Override
    public TextSnippetEntity save(TextSnippetEntity textSnippetEntity){
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
    public List<TextSnippetEntity>findByText(final String text){
        return dao.findByText(text);
    }

    @Transactional
    public List findGlobalAndByProject(Long id) {
        return dao.findGlobalAndByProject(id);
    }
}

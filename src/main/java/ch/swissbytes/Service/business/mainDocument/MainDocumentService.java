package ch.swissbytes.Service.business.mainDocument;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
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
public class MainDocumentService extends Service<MainDocumentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(MainDocumentService.class.getName());

    @Inject
    private MainDocumentDao dao;

    @Inject
    private AttachmentMainDocumentService attachmentService;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    @Transactional
    public void delete(MainDocumentEntity entity) {
        entity.setStatus(StatusEnum.DELETED);
        entity.setLastUpdate(new Date());
        dao.update(entity);
    }

    @Transactional
    public void doSave(MainDocumentEntity entity){
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setCode(entity.getCode().toUpperCase());
        super.doSave(entity);
    }

    @Transactional
    public void doSaveWithPdf(MainDocumentEntity entity,AttachmentMainDocumentEntity attachmentMainDocumentEntity){
        attachmentService.doSave(attachmentMainDocumentEntity);
        entity.setAttachmentMainDocument(attachmentMainDocumentEntity);
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setCode(entity.getCode().toUpperCase());
        super.doSave(entity);
    }


    @Override
    public MainDocumentEntity save(MainDocumentEntity entity){
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        return super.save(entity);
    }

    @Transactional
    public MainDocumentEntity findById(Long id) {
        List<MainDocumentEntity> list = dao.findById(MainDocumentEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Transactional
    public List<MainDocumentEntity> getMainDocumentList() {
        return dao.getMainDocumentList();
    }

    @Transactional
    public boolean isCodeDuplicated(Long id, String code) {
        return !dao.findByCodeButWithNoId(code, id).isEmpty();
    }

    @Transactional
    public List<MainDocumentEntity>findByText(final String code){
        return dao.findByCode(code);
    }

    @Transactional
    public List findByProjectId() {
        return dao.findByProject();
    }

    public AttachmentMainDocumentEntity findAttachmentMainDocument(Long attachmentMainDocumentId) {
        return attachmentService.findById(attachmentMainDocumentId);
    }
}

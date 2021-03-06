package ch.swissbytes.Service.business.mainDocument;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
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
public class AttachmentMainDocumentService extends Service<AttachmentMainDocumentEntity> implements Serializable {

    @Inject
    private AttachmentMainDocumentDao dao;

    @Transactional
    public void delete(AttachmentMainDocumentEntity entity) {
        dao.delete(entity);
    }
    @Transactional
    public void doSave(AttachmentMainDocumentEntity entity){
        dao.doSave(entity);
    }

    @Transactional
    public AttachmentMainDocumentEntity findById(Long id) {
        List<AttachmentMainDocumentEntity> list = dao.findById(AttachmentMainDocumentEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

}

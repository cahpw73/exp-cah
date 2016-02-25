package ch.swissbytes.Service.business.mainDocument;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by christian on 27/01/16.
 */

public class AttachmentMainDocumentDao extends GenericDao<AttachmentMainDocumentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(AttachmentMainDocumentDao.class.getName());


    public void doSave(AttachmentMainDocumentEntity entity){
        super.saveAndFlush(entity);
    }

    public void doUpdate(AttachmentMainDocumentEntity detachedEntity){
        AttachmentMainDocumentEntity entity = super.merge(detachedEntity);
       super.update(entity);
    }

    public List<MainDocumentEntity> getMainDocumentList(){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM MainDocumentEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("ORDER BY x.description ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }


    public List<MainDocumentEntity> findByCodeButWithNoId(String code, Long id){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM MainDocumentEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("AND trim(lower(x.code))=:CODE ");
        sb.append("AND NOT x.id=:MAIN_DOC_ID ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("CODE", code.toLowerCase().trim());
        map.put("MAIN_DOC_ID", id!=null?id:0L);
        return super.findBy(sb.toString(),map);
    }

    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }

}

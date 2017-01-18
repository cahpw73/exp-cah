package ch.swissbytes.Service.business.poDocument;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.PODocumentEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderProcurementEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by christian on 9/10/14.
 */

public class PODocumentDao extends GenericDao<PODocumentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(PODocumentDao.class.getName());

    public void doUpdate(PODocumentEntity detachedEntity){
        PODocumentEntity entity = super.merge(detachedEntity);
        super.update(entity);
    }

    public void doSave(PODocumentEntity entity) {
        super.saveAndFlush(entity);
    }

    @Transactional
    public List<PODocumentEntity> findByPOId(final Long poEntityId) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM PODocumentEntity x ");
        sb.append(" WHERE x.status = :ENABLED ");
        sb.append(" AND x.poProcurementEntity.id = :PO_ID ");
        sb.append(" ORDER BY x.ordered ");
        Map<String,Object> map=new HashMap<>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("PO_ID", poEntityId);
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

    public List<PODocumentEntity> findByProjectDocumentId(final Long projectDocId) {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM PODocumentEntity x ");
        sb.append(" WHERE x.status = :ENABLED ");
        sb.append(" AND x.projectDocumentEntity.id = :PROJECT_DOC_ID ");
        Map<String,Object> map=new HashMap<>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("PROJECT_DOC_ID", projectDocId);
        return super.findBy(sb.toString(),map);
    }

    public List<PODocumentEntity> getAll() {
        StringBuilder sb=new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM PODocumentEntity x ");
        Map<String,Object> map=new HashMap<>();
        return super.findBy(sb.toString(),map);
    }
}

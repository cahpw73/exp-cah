package ch.swissbytes.Service.business.expeditingStatus;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ExpeditingStatusEntity;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */

public class ExpeditingStatusDao extends GenericDao<ExpeditingStatusEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ExpeditingStatusDao.class.getName());

    public void doSave(ExpeditingStatusEntity entity) {
        super.saveAndFlush(entity);
    }

    public void doUpdate(ExpeditingStatusEntity detachedEntity) {
        ExpeditingStatusEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public void doRemove(ExpeditingStatusEntity detachedEntity) {
        ExpeditingStatusEntity entity = super.merge(detachedEntity);
        super.delete(entity);
    }

    public List<ExpeditingStatusEntity> findByPOIds(final Long poId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT e ");
        sb.append(" FROM ExpeditingStatusEntity e ");
        sb.append(" WHERE e.purchaseOrderEntity.id = :PO_ID ");
        Map<String, Object> params = new HashMap<>();
        params.put("PO_ID", poId);
        return super.findBy(sb.toString(), params);
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

    public List<ExpeditingStatusEntity> findByPOIdAndStatusIssued(Long poId, ExpeditingStatusEnum issued) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT e ");
        sb.append(" FROM ExpeditingStatusEntity e ");
        sb.append(" WHERE e.purchaseOrderEntity.id = :PO_ID ");
        sb.append(" AND e.purchaseOrderStatus = :ISSUED ");
        Map<String, Object> params = new HashMap<>();
        params.put("PO_ID", poId);
        params.put("ISSUED", issued);
        return super.findBy(sb.toString(), params);
    }
}

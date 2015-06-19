package ch.swissbytes.Service.business.purchase;

import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */

public class PurchaseOrderDao extends GenericDao<PurchaseOrderEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(PurchaseOrderDao.class.getName());


    public PurchaseOrderEntity load(final Long id) {
        return super.load(PurchaseOrderEntity.class, id);
    }

    @Override
    protected void applyCriteriaValues(Query query, Filter f) {
        log.info("protected  void applyCriteriaValues(Query query,Filter f)");
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        if (f != null) {
            log.info("filtering.....");
            SearchPurchase filter = (SearchPurchase) f;
            if (StringUtils.isNotBlank(filter.getProject()) && StringUtils.isNotEmpty(filter.getProject())) {
                query.setParameter("PROJECT", "%" + filter.getProject().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getPo()) && StringUtils.isNotEmpty(filter.getPo())) {
                query.setParameter("PO", "%" + filter.getPo().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getVariation()) && StringUtils.isNotEmpty(filter.getVariation())) {
                query.setParameter("VARIATION", "%" + filter.getVariation().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getPoTitle()) && StringUtils.isNotEmpty(filter.getPoTitle())) {
                query.setParameter("PO_TITLE", "%" + filter.getPoTitle().trim() + "%");
            }
        } else {
            log.info("filter is null");
        }
    }

    protected String getEntity() {
        return PurchaseOrderEntity.class.getSimpleName();
    }

    @Override
    protected String addCriteria(Filter f) {
        log.info("protected  String addCriteria(Filter f)");
        StringBuilder sb = new StringBuilder();
        sb.append(" AND x.status.id<>:DELETED ");
        if (f != null) {
            log.info("filtering.....");
            SearchPurchase filter = (SearchPurchase) f;
            if (StringUtils.isNotBlank(filter.getProject()) && StringUtils.isNotEmpty(filter.getProject())) {
                sb.append(" AND lower(x.project) like lower(:PROJECT)");
            }
            if (StringUtils.isNotBlank(filter.getPo()) && StringUtils.isNotEmpty(filter.getPo())) {
                sb.append(" AND lower(x.po) like lower(:PO)");
            }
            if (StringUtils.isNotBlank(filter.getPoTitle()) && StringUtils.isNotEmpty(filter.getPoTitle())) {
                sb.append(" AND lower(x.poTitle) like lower(:PO_TITLE)");
            }
            if (StringUtils.isNotBlank(filter.getVariation()) && StringUtils.isNotEmpty(filter.getVariation())) {
                sb.append(" AND lower(x.variation) like lower(:VARIATION)");
            }
        } else {
            log.info("filter is null");
        }
        return sb.toString();
    }


    public List<PurchaseOrderEntity> findPOByProject(Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id=:PROJECT_ID ");
        sb.append(" ORDER BY po.po ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOByProjectIdAndPoNo(final Long projectId, final String poNo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        if(projectId != null){
            sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
            map.put("PROJECT_ID", projectId);
        }
        if (StringUtils.isNotEmpty(poNo) && StringUtils.isNotBlank(poNo)) {
            sb.append(" AND LOWER(po.poEntity.orderNumber) LIKE :ORDER_NUMBER");
            map.put("ORDER_NUMBER", "%" + poNo.toLowerCase().trim() + "%");
        }
        sb.append(" ORDER BY po.po ");

        return super.findBy(sb.toString(), map);
    }

    public POEntity savePOEntity(POEntity poEntity) {
        entityManager.persist(poEntity);
        return poEntity;
    }

    public POEntity updatePOEntity(POEntity poEntity) {
        POEntity entityManaged = entityManager.merge(poEntity);
        entityManager.persist(entityManaged);
        return entityManaged;
    }

    @Override
    public String orderBy(String field, boolean ascending) {
        return "ORDER BY " + field;
    }

    public List<PurchaseOrderEntity> findByProjectAndPo(Long projectId, String poNo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id  = :PROJECT_ID");
        sb.append(" AND po.po = :PO_NO ");
        sb.append(" ORDER BY po.po ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PROJECT_ID", projectId);
        map.put("PO_NO", poNo);
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findByVariation(String variation, String poNumber,Long id){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND lower(po.variation)  = :VARIATION");
        sb.append(" AND lower(po.po) = :PO_NUMBER ");
        sb.append(" AND NOT po.id = :ID ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("VARIATION", variation!=null?variation.trim().toLowerCase():"");
        map.put("PO_NUMBER",poNumber==null?"":poNumber.trim().toLowerCase());
        map.put("ID",id==null?"":id!=null?id:0);
        return super.findBy(sb.toString(), map);
    }
}

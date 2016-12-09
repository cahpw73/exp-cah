package ch.swissbytes.Service.business.purchase;

import ch.swissbytes.Service.infrastructure.GenericDao;

import ch.swissbytes.domain.model.entities.PurchaseOrderProcurementEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.VPurchaseOrder;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.types.ClassEnum;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.domain.types.ProcurementStatus;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.procurement.boundary.purchaseOrder.FilterPO;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.export.DataExporter;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
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
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        if (f != null) {
            FilterPO filter = (FilterPO) f;
            if (filter.getProjectId() != null) {
                query.setParameter("PROJECT_ID", filter.getProjectId());
            }
        }
    }

    protected String getEntity() {
        return PurchaseOrderEntity.class.getSimpleName();
    }

    @Override
    protected String addCriteria(Filter f) {
        StringBuilder sb = new StringBuilder();
        sb.append(" AND x.status.id<>:DELETED ");
        if (f != null) {
            FilterPO filter = (FilterPO) f;
            if (filter.getProjectId() != null) {
                sb.append(" AND x.projectEntity.id =:PROJECT_ID");
            }
        }
        return sb.toString();
    }


    public List<PurchaseOrderEntity> findPOByProject(Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id=:PROJECT_ID ");
        sb.append(" ORDER BY po.po,po.orderedVariation ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOByIdOnly(Long poId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.id=:PO_ID ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PO_ID", poId);
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOMaxVariations(Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po.po,MAX(po.orderedVariation) ");
        sb.append(" FROM PurchaseOrderEntity po");
        sb.append(" WHERE po.status.id = :ENABLED ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        //  sb.append(" AND po.purchaseOrderProcurementEntity.id = p.id ");
        sb.append(" GROUP BY po.po ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOWithMaxVariation(String project, String po) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po, PurchaseOrderProcurementEntity p ");
        sb.append(" WHERE po.status.id = :ENABLED ");
        sb.append(" AND po.project = :PROJECT ");
        sb.append(" AND po.po = :PO ");
        sb.append(" AND p.poProcStatus = :INCOMPLETE ");
        sb.append(" AND po.purchaseOrderProcurementEntity.id = p.id ");
        sb.append(" AND po.orderedVariation = (");
        sb.append(" SELECT MAX(po.orderedVariation) ");
        sb.append(" FROM PurchaseOrderEntity po, PurchaseOrderProcurementEntity p ");
        sb.append(" WHERE po.status.id = :ENABLED ");
        sb.append(" AND po.project = :PROJECT ");
        sb.append(" AND po.po = :PO ");
        sb.append(" AND p.poProcStatus = :INCOMPLETE ");
        sb.append(" AND po.purchaseOrderProcurementEntity.id = p.id ");
        sb.append(" )");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("PROJECT", project);
        map.put("PO", po);
        map.put("INCOMPLETE", ProcurementStatus.INCOMPLETE);
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOsBy(FilterPO filterPO) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE  po.projectEntity.id = :PROJECT ");
        sb.append(" AND po.status.id = :ENABLED ");
        if (filterPO.getClassEnum() != null &&
                (filterPO.getClassEnum().ordinal() == ClassEnum.CONSTRUCTION_CONTRACT.ordinal() ||
                        filterPO.getClassEnum().ordinal() == ClassEnum.SERVICE_CONTRACT.ordinal() ||
                        filterPO.getClassEnum().ordinal() == ClassEnum.MINING_FLEET.ordinal())) {
            sb.append(" AND po.purchaseOrderProcurementEntity.clazz = :CLAZZ ");
        }
        if (filterPO.getPurchaseOrderNumberOption() != null) {
            sb.append(" AND (po.po >= :BEGINNING ");
            sb.append(" AND po.po <= :ENDING )");
        }
        sb.append(" ORDER BY po.po, po.orderedVariation ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("PROJECT", filterPO.getProjectId());
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        if (filterPO.getClassEnum() != null &&
                (filterPO.getClassEnum().ordinal() == ClassEnum.CONSTRUCTION_CONTRACT.ordinal() ||
                        filterPO.getClassEnum().ordinal() == ClassEnum.SERVICE_CONTRACT.ordinal() ||
                        filterPO.getClassEnum().ordinal() == ClassEnum.MINING_FLEET.ordinal())) {
            map.put("CLAZZ", filterPO.getClassEnum());
        }
        if (filterPO.getPurchaseOrderNumberOption() != null) {
            map.put("BEGINNING", filterPO.getPurchaseOrderNumberOption().getBeginning());
            map.put("ENDING", filterPO.getPurchaseOrderNumberOption().getEnding());
        }
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOByOneVariation(String project, String po, String variation) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po, PurchaseOrderProcurementEntity p ");
        sb.append(" WHERE po.variation = :VARIATION ");
        sb.append(" AND po.project = :PROJECT ");
        sb.append(" AND po.po = :PO ");
        sb.append(" AND po.status.id = :ENABLED ");
        sb.append(" AND po.purchaseOrderProcurementEntity.id = p.id ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("PROJECT", project);
        map.put("PO", po);
        map.put("VARIATION", variation);
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOByProjectIdAndPoNo(final Long projectId, final String poNo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        if (projectId != null) {
            sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
            map.put("PROJECT_ID", projectId);
        }
        if (StringUtils.isNotEmpty(poNo) && StringUtils.isNotBlank(poNo)) {
            sb.append(" AND LOWER(po.po) LIKE :ORDER_NUMBER");
            map.put("ORDER_NUMBER", "%" + poNo.toLowerCase().trim() + "%");
        }
        sb.append(" ORDER BY po.po ");

        return super.findBy(sb.toString(), map);
    }

    public PurchaseOrderProcurementEntity savePOEntity(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity) {
        entityManager.persist(purchaseOrderProcurementEntity);
        return purchaseOrderProcurementEntity;
    }

    public PurchaseOrderProcurementEntity updatePOEntity(PurchaseOrderProcurementEntity purchaseOrderProcurementEntity) {
        PurchaseOrderProcurementEntity entityManaged = entityManager.merge(purchaseOrderProcurementEntity);
        entityManager.persist(entityManaged);
        return entityManaged;
    }

    public PurchaseOrderProcurementEntity findPOEntityById(final Long poId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM  PurchaseOrderProcurementEntity p ");
        sb.append(" WHERE p.id = :PO_ID ");
        Map<String, Object> map = new HashMap<>();
        map.put("PO_ID", poId);
        List<PurchaseOrderProcurementEntity> list = super.findBy(sb.toString(), map);
        PurchaseOrderProcurementEntity entity = null;
        if (!list.isEmpty()) {
            entity = list.get(0);
        }
        return entity;
    }

    @Override
    public String orderBy(String field, boolean ascending) {
        return " ORDER BY x.po,x.orderedVariation ";
    }

    @Override
    public String orderBy() {
        return " ORDER BY x.po,x.orderedVariation ";
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

    public List<PurchaseOrderEntity> findPOListWithoutExportCMS(Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po LEFT JOIN po.purchaseOrderProcurementEntity p");
        sb.append(" WHERE po.status.id = :ENABLED ");
        sb.append(" AND po.projectEntity.id  = :PROJECT_ID");
        sb.append(" AND ( p.cmsExported = false OR p.cmsExported = null)");
        sb.append(" AND p.poProcStatus = :COMMITTED");
        sb.append(" ORDER BY po.po, po.orderedVariation ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PROJECT_ID", projectId);
        map.put("COMMITTED",ProcurementStatus.COMMITTED);
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findPOListWithoutExportJDE(Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po LEFT JOIN po.purchaseOrderProcurementEntity p");
        sb.append(" WHERE po.status.id = :ENABLED ");
        sb.append(" AND po.projectEntity.id  = :PROJECT_ID");
        sb.append(" AND ( p.jdeExported = false OR p.jdeExported = null)");
        sb.append(" AND p.poProcStatus = :COMMITTED");
        sb.append(" ORDER BY po.po, po.orderedVariation ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PROJECT_ID", projectId);
        map.put("COMMITTED",ProcurementStatus.COMMITTED);
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findByVariation(PurchaseOrderEntity purchaseOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND lower(po.variation)  = :VARIATION");
        sb.append(" AND lower(po.po) = :PO_NUMBER ");
        sb.append(" AND NOT po.id = :ID ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("VARIATION", purchaseOrder.getVariation() != null ? purchaseOrder.getVariation().trim().toLowerCase() : "");
        map.put("PO_NUMBER", purchaseOrder.getPo() == null ? "" : purchaseOrder.getPo().trim().toLowerCase());
        map.put("ID", purchaseOrder.getId() == null ? -1L : purchaseOrder.getId());
        map.put("PROJECT_ID", purchaseOrder.getProjectEntity().getId());
        return super.findBy(sb.toString(), map);
    }

    public List<PurchaseOrderEntity> findByProjectCustomizedSort(Long projectId, Map<String, Boolean> sortByMap) {
        Boolean poNo = sortByMap.get("poNo");
        Boolean supplier = sortByMap.get("supplier");
        Boolean deliveryDate = sortByMap.get("deliveryDate");
        String strSort = "";
        if (poNo) {
            strSort = strSort + "p.po,";
        }
        if (supplier) {
            strSort = strSort + "sp.company,";
        }
        if (deliveryDate) {
            strSort = strSort + "p.poDeliveryDate,";
        }
        if (strSort.length() > 1) {
            strSort = strSort.substring(0, strSort.length() - 1);
        }

        log.info(" sort by: " + strSort);

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p.po, p.variation, po.orderDate,sp.company,po.orderTitle,c.code, p.poDeliveryDate,po.poProcStatus ");
        sb.append(" FROM PurchaseOrderEntity p ");
        sb.append(" INNER JOIN p.purchaseOrderProcurementEntity po ");
        sb.append(" LEFT JOIN po.supplier sp ");
        sb.append(" LEFT JOIN po.currency pc ");
        sb.append(" LEFT JOIN pc.currency c");
        sb.append(" WHERE p.status.id=:ENABLED ");
        sb.append(" AND p.projectEntity.id  = :PROJECT_ID");
        if (poNo || supplier || deliveryDate) {
            sb.append(" ORDER BY " + strSort + " ");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ENABLED", StatusEnum.ENABLE.getId());
        map.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), map);
    }

    public VPurchaseOrder findVPOById(Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT v ");
        sb.append("FROM VPurchaseOrder v ");
        sb.append("WHERE v.poId=:ID ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ID", id);
        List<VPurchaseOrder> list = this.findBy(sb.toString(), parameters);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<PurchaseOrderEntity> findAllPOs(final Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        sb.append(" ORDER BY po.po ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENABLED", StatusEnum.ENABLE.getId());
        parameters.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), parameters);
    }

    public int getTotalNumberOfPOs(final Long projectId) {
        log.info("getTotalNumberOfPOs(Long projectId["+projectId+"])");
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(po.id) ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id = :ENABLED ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        sb.append(" AND po.purchaseOrderProcurementEntity.poProcStatus = :COMMITTED ");
        sb.append(" AND NOT po.id IN ( ");
        sb.append(" SELECT ex.purchaseOrderEntity.id ");
        sb.append(" FROM ExpeditingStatusEntity ex ");
        sb.append(" WHERE ex.purchaseOrderStatus = :CANCELLED ");
        sb.append(" OR ex.purchaseOrderStatus = :DELETED) ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENABLED", StatusEnum.ENABLE.getId());
        parameters.put("PROJECT_ID", projectId);
        parameters.put("COMMITTED",ProcurementStatus.COMMITTED);
        parameters.put("DELETED", ExpeditingStatusEnum.DELETED);
        parameters.put("CANCELLED", ExpeditingStatusEnum.CANCELLED);
        List<PurchaseOrderEntity> list = super.findBy(sb.toString(), parameters);
        Object object = list.get(0);
        Long result = (Long) object;
        return result.intValue();
    }

    public int getNumberOfCompletedPOs(final Long projectId) {
        log.info("getNumberOfCompletedPOs(Long projectId["+projectId+"])");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(po.id) ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        sb.append(" AND po.purchaseOrderProcurementEntity.poProcStatus = :COMMITTED ");
        sb.append(" AND po.id IN ( ");
        sb.append(" SELECT ex.purchaseOrderEntity.id ");
        sb.append(" FROM ExpeditingStatusEntity ex ");
        sb.append(" WHERE ex.purchaseOrderStatus = :COMPLETED) ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENABLED", StatusEnum.ENABLE.getId());
        parameters.put("COMPLETED", ExpeditingStatusEnum.COMPLETED);
        parameters.put("COMMITTED", ProcurementStatus.COMMITTED);
        parameters.put("PROJECT_ID", projectId);
        List<PurchaseOrderEntity> list = super.findBy(sb.toString(), parameters);
        Object object = list.get(0);
        Long result = (Long) object;
        return result.intValue();
    }

    public int getNumberOfOpenPOs(Long projectId) {
        log.info("getNumberOfOpenPOs(Long projectId["+projectId+"])");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(po.id) ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        sb.append(" AND po.purchaseOrderProcurementEntity.poProcStatus = :COMMITTED ");
        sb.append(" AND NOT po.id IN ( ");
        sb.append(" SELECT ex.purchaseOrderEntity.id ");
        sb.append(" FROM ExpeditingStatusEntity ex ");
        sb.append(" WHERE ex.purchaseOrderStatus = :COMPLETED ");
        sb.append(" OR ex.purchaseOrderStatus = :DELETED ");
        sb.append(" OR ex.purchaseOrderStatus = :CANCELLED) ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENABLED", StatusEnum.ENABLE.getId());
        parameters.put("COMPLETED", ExpeditingStatusEnum.COMPLETED);
        parameters.put("DELETED", ExpeditingStatusEnum.DELETED);
        parameters.put("CANCELLED", ExpeditingStatusEnum.CANCELLED);
        parameters.put("COMMITTED", ProcurementStatus.COMMITTED);
        parameters.put("PROJECT_ID", projectId);
        List<PurchaseOrderEntity> list = super.findBy(sb.toString(), parameters);
        Object object = list.get(0);
        Long result = (Long) object;
        return result.intValue();
    }

    public int getNumberDeliveryNextMoth(Long projectId,Date nextMothIni,Date nextMothEnd) {
        log.info("parameters: id["+projectId+"], ini["+nextMothIni+"],end["+nextMothEnd+"]");
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT COUNT(sp.id) ");
        sb.append(" FROM ScopeSupplyEntity sp INNER JOIN sp.purchaseOrder po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        sb.append(" AND sp.status.id = :ENABLED ");
        sb.append(" AND po.purchaseOrderProcurementEntity.poProcStatus = :COMMITTED ");
        sb.append(" AND sp.forecastSiteDate <= :NEXT_MOTH_END ");
        sb.append(" AND sp.forecastSiteDate >= :NEXT_MOTH_INI ");
        sb.append(" AND NOT po.id IN ( ");
        sb.append(" SELECT ex.purchaseOrderEntity.id ");
        sb.append(" FROM ExpeditingStatusEntity ex ");
        sb.append(" WHERE ex.purchaseOrderStatus = :COMPLETED ");
        sb.append(" OR po.purchaseOrderStatus = :DELETED ");
        sb.append(" OR po.purchaseOrderStatus = :CANCELLED ");
        sb.append(" OR po.purchaseOrderStatus = :MMR_REQUIRED) ");
        sb.append(" GROUP BY po.id,sp.forecastSiteDate");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENABLED", StatusEnum.ENABLE.getId());
        parameters.put("COMPLETED", ExpeditingStatusEnum.COMPLETED);
        parameters.put("DELETED", ExpeditingStatusEnum.DELETED);
        parameters.put("CANCELLED", ExpeditingStatusEnum.CANCELLED);
        parameters.put("MMR_REQUIRED",ExpeditingStatusEnum.MMR_REQUIRED);
        parameters.put("COMMITTED", ProcurementStatus.COMMITTED);
        parameters.put("PROJECT_ID", projectId);
        parameters.put("NEXT_MOTH_INI", nextMothIni);
        parameters.put("NEXT_MOTH_END", nextMothEnd);
        parameters.put("ENABLED", StatusEnum.ENABLE.getId());
        List<PurchaseOrderEntity> list = super.findBy(sb.toString(), parameters);
        if(list.size()>0) {
            return list.size();
        }else{
            return 0;
        }
    }

    public int getNumberMrrOutstanding(Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(po.id) ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.status.id=:ENABLED ");
        sb.append(" AND po.projectEntity.id = :PROJECT_ID ");
        sb.append(" AND po.purchaseOrderProcurementEntity.poProcStatus = :COMMITTED ");
        sb.append(" AND po.id IN ( ");
        sb.append(" SELECT ex.purchaseOrderEntity.id ");
        sb.append(" FROM ExpeditingStatusEntity ex ");
        sb.append(" WHERE ex.purchaseOrderStatus = :MMR_REQUIRED) ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENABLED", StatusEnum.ENABLE.getId());
        parameters.put("MMR_REQUIRED", ExpeditingStatusEnum.MMR_REQUIRED);
        parameters.put("COMMITTED", ProcurementStatus.COMMITTED);
        parameters.put("PROJECT_ID", projectId);
        List<PurchaseOrderEntity> list = super.findBy(sb.toString(), parameters);
        Object object = list.get(0);
        Long result = (Long)object;
        return result.intValue();
    }

    public void resetActivity(PurchaseOrderEntity purchaseOrderEntity) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE PurchaseOrderEntity ");
        sb.append("SET  lastActivityUpdate =:DATE ");
        sb.append("WHERE ID=:PO_ID ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("DATE", new Date());
        parameters.put("PO_ID", purchaseOrderEntity.getId());
        super.executeUpdate(sb.toString(), parameters);
    }

    public void lockPO(PurchaseOrderEntity purchaseOrderEntity, UserEntity userEntity) {
        updateLock(purchaseOrderEntity, userEntity, true);
    }

    private void updateLock(PurchaseOrderEntity purchaseOrderEntity, UserEntity userEntity, boolean lock) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE PurchaseOrderEntity ");
        sb.append("SET lastActivityUpdate =:DATE, ");
        sb.append("lockedBy =:USER, ");
        sb.append("locked =:LOCKED ");
        sb.append("WHERE ID=:PO_ID ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("DATE", new Date());
        parameters.put("PO_ID", purchaseOrderEntity.getId());
        parameters.put("USER", userEntity);
        parameters.put("LOCKED", lock);
        super.executeUpdate(sb.toString(), parameters);
    }

    public void unLockPO(PurchaseOrderEntity purchaseOrderEntity) {
        updateLock(purchaseOrderEntity, null, false);
    }

    public List<PurchaseOrderEntity> findFirstPO(final PurchaseOrderEntity purchaseOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.po= :PO_NUMBER ");
        sb.append(" AND  po.projectEntity.id = :PROJECT_ID ");
        sb.append(" AND po.orderedVariation = 1 ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("PO_NUMBER", purchaseOrder.getPo());
        parameters.put("PROJECT_ID", purchaseOrder.getProjectEntity().getId());
        return super.findBy(sb.toString(), parameters);
    }

    public List<PurchaseOrderEntity> findPreviousRevisionPO(Long projectId, String po, Integer previousOrderedVariation) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE po.po= :PO_NUMBER ");
        sb.append(" AND  po.projectEntity.id = :PROJECT_ID ");
        sb.append(" AND po.orderedVariation = :ORDERED_VARIATION ");
        sb.append("AND po.status.id=:ENABLED");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("PO_NUMBER", po);
        parameters.put("PROJECT_ID", projectId);
        parameters.put("ORDERED_VARIATION",previousOrderedVariation);
        parameters.put("ENABLED",StatusEnum.ENABLE.getId());
        return super.findBy(sb.toString(), parameters);
    }

    public List<PurchaseOrderEntity> findByPOProcurementId(final Long poProcurementId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT po ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" WHERE  po.purchaseOrderProcurementEntity.id = :PO_PROCUREMENT_ID");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("PO_PROCUREMENT_ID",poProcurementId);
        return super.findBy(sb.toString(), parameters);
    }
}

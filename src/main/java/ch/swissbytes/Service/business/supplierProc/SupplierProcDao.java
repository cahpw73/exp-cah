package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alvaro on 9/8/14.
 */


public class SupplierProcDao extends GenericDao<SupplierProcEntity> implements Serializable {


    public SupplierProcEntity findSupplierBySupplierIdAndDistinct(String supplierId, Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT s ");
        sb.append(" FROM SupplierProcEntity s ");
        sb.append(" WHERE s.status = :ENABLE ");
        sb.append(" AND NOT s.id = :ID ");
        sb.append(" AND trim(lower(s.supplierId)) = :SUPPLIER_ID ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("SUPPLIER_ID", supplierId.trim().toLowerCase());
        params.put("ID", id == null ? 0 : id);
        List<SupplierProcEntity> list = super.findBy(sb.toString(), params);
        return list.isEmpty() ? null : list.get(0);
    }

    public Integer findPageByCurrentSupplier(BigInteger supplierId, String terms, Integer defaultSizePage) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT page_number ");
        sb.append(" FROM ( ");
        sb.append(" SELECT id,company,row_number() over (order by supplier_id),(row_number() over (order by supplier_id)-1)/" + defaultSizePage + " as page_number ");
        sb.append(" FROM supplier_proc sp ");
        sb.append(" WHERE 1=1");
        if (StringUtils.isNotEmpty(terms)) {
            sb.append(" AND ( lower(company) like '" + terms + "%'  ");
            sb.append(" OR lower(supplier_id) like '" + terms + "%' )");
        }
        sb.append(" ORDER BY supplier_id ");
        sb.append(" ) queryA ");
        sb.append(" WHERE id=" + supplierId + " ");
        Query query = entityManager.createNativeQuery(sb.toString());
        List<Object> list = query.getResultList();
        BigInteger numberPage = !list.isEmpty() ? (BigInteger) list.get(0) : BigInteger.ZERO;
        return numberPage.intValue();
    }


    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {
        if (filter != null && StringUtils.isNotEmpty(filter.getCriteria()) && StringUtils.isNotBlank(filter.getCriteria())) {
            query.setParameter("CRITERIA", filter.getCriteria().trim().toLowerCase() + "%");
        }
        query.setParameter("ENABLED", StatusEnum.ENABLE);
    }

    @Override
    protected String getEntity() {
        return SupplierProcEntity.class.getName();
    }

    @Override
    protected String addCriteria(Filter filter) {
        StringBuilder query = new StringBuilder();
        if (filter != null && StringUtils.isNotEmpty(filter.getCriteria()) && StringUtils.isNotBlank(filter.getCriteria())) {
            query.append(" AND (");
            query.append(" lower(x.supplierId) LIKE :CRITERIA ");
            query.append(" OR lower(x.company) LIKE :CRITERIA ");
            query.append(") ");
        }
        query.append(" AND x.status=:ENABLED ");
        return query.toString();
    }

    @Override
    public String orderBy() {
        return "";
    }

    @Override
    public String orderBy(String field, boolean ascending) {
        return " ORDER BY " + field + (ascending ? " ASC " : " DESC");
    }

    public List<SupplierProcEntity> findByCompany(String company) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT s ");
        sb.append(" FROM SupplierProcEntity s ");
        sb.append(" WHERE s.status = :ENABLE ");
        sb.append(" AND LOWER(s.company) = :COMPANY ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("COMPANY", company.trim().toLowerCase());
        return super.findBy(sb.toString(), params);
    }

    public List<SupplierProcEntity> findAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT s ");
        sb.append(" FROM SupplierProcEntity s ");
        sb.append(" WHERE s.status = :ENABLE ");
        sb.append(" ORDER BY s.supplierId ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<SupplierProcEntity> findSuppliersActives() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT s ");
        sb.append(" FROM SupplierProcEntity s ");
        sb.append(" WHERE s.status = :ENABLE ");
        sb.append(" AND s.active = true ");
        sb.append(" ORDER BY s.supplierId ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<String> findCountriesByCategory(final Long categoryId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT sc.supplier.country ");
        sb.append(" FROM SupplierCategory sc ");
        sb.append(" WHERE sc.status = :ENABLE ");
        sb.append(" AND sc.category.id = :CATEGORY_ID ");
        sb.append(" AND sc.supplier.country IS NOT NULL and length(trim(sc.supplier.country))>0");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("CATEGORY_ID", categoryId != null ? categoryId : 0);
        return super.findBy(sb.toString(), params);
    }

    public List<SupplierProcEntity> findSupplierByCountriesAndCategory(final Long categoryId, List<String> countries) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT sc.supplier ");
        sb.append(" FROM SupplierCategory sc ");
        sb.append(" WHERE sc.status = :ENABLE ");
        sb.append(" AND sc.category.id = :CATEGORY_ID ");
        if (countries != null && !countries.isEmpty()) {
            sb.append(" AND sc.supplier.country IN (:COUNTRIES)");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("CATEGORY_ID", categoryId != null ? categoryId : 0);
        if (countries != null && !countries.isEmpty()) {
            params.put("COUNTRIES", countries);
        }
        return super.findBy(sb.toString(), params);
    }

    public List<SupplierProcEntity> findSupplierByProjectsAndCategory(final Long categoryId, final Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT distinct p.supplier ");
        sb.append(" FROM PurchaseOrderEntity po  ");
        sb.append(" LEFT JOIN po.purchaseOrderProcurementEntity p ");
        sb.append(" LEFT JOIN p.supplier sp ");
        sb.append(" WHERE ");
      //  sb.append(" po.projectEntity.id = :PROJECT_ID ");
        sb.append(" po.status.id=:ENABLE_ID ");
        sb.append(" AND sp.status=:ENABLE ");
        sb.append(" AND sp.id IN( ");
        sb.append(" SELECT sc.supplier.id ");
        sb.append(" FROM SupplierCategory sc ");
        sb.append(" WHERE sc.status = :ENABLE ");
        sb.append(" AND sc.category.id = :CATEGORY_ID ");
        sb.append(" AND sc.supplier.country IS NOT NULL and length(trim(sc.supplier.country))>0 ");
        sb.append(" ) ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("ENABLE_ID",StatusEnum.ENABLE.getId());
       // params.put("PROJECT_ID", projectId);
        params.put("CATEGORY_ID", categoryId != null ? categoryId : 0);
        return super.findBy(sb.toString(), params);
    }

    public List<SupplierProcEntity> getSupplierList(List<SupplierProcEntity> list){
        List<Long> ids = new ArrayList<>();
        for (SupplierProcEntity p : list){
            ids.add(p.getId());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT s ");
        sb.append(" FROM SupplierProcEntity s  ");
        sb.append(" WHERE s.id IN :IDS ");
        sb.append(" ORDER BY s.id ");

        Map<String, Object> params = new HashMap<>();
        params.put("IDS", ids);
        return super.findBy(sb.toString(), params);
    }

}

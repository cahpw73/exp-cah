package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
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
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("SUPPLIER_ID", supplierId.trim().toLowerCase());
        params.put("ID",id==null?0:id);
        List<SupplierProcEntity> list=super.findBy(sb.toString(),params);
        return list.isEmpty()?null:list.get(0);
    }


    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {
        if (filter != null && StringUtils.isNotEmpty(filter.getCriteria()) && StringUtils.isNotBlank(filter.getCriteria())) {
            query.setParameter("CRITERIA", "%" + filter.getCriteria().trim().toLowerCase() + "%");
        }
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
            query.append(" OR lower(x.state) LIKE :CRITERIA ");
            query.append(" OR lower(x.country) LIKE :CRITERIA ");
            query.append(") ");
        }
        return query.toString();
    }

    @Override
    public String orderBy(){
        return " ORDER BY x.supplierId , x.company";
    }

    public List<SupplierProcEntity> findByCompany(String company) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT s ");
        sb.append(" FROM SupplierProcEntity s ");
        sb.append(" WHERE s.status = :ENABLE ");
        sb.append(" AND LOWER(s.company) = :COMPANY ");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("COMPANY", company.trim().toLowerCase());
        return super.findBy(sb.toString(),params);
    }

    public List<SupplierProcEntity> findAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT s ");
        sb.append(" FROM SupplierProcEntity s ");
        sb.append(" WHERE s.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }
}

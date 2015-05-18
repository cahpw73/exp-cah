package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;

/**
 * Created by alvaro on 9/8/14.
 */


public class SupplierProcDao extends GenericDao<SupplierProcEntity> implements Serializable {





    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {
        if(filter!=null&&StringUtils.isNotEmpty(filter.getCriteria())&&StringUtils.isNotBlank(filter.getCriteria())){
            query.setParameter("CRITERIA",filter.getCriteria().trim().toLowerCase());
        }
    }

    @Override
    protected String getEntity() {
        return SupplierProcEntity.class.getName();
    }

    @Override
    protected String addCriteria(Filter filter) {
        StringBuilder query=new StringBuilder();
        if(filter!=null&&StringUtils.isNotEmpty(filter.getCriteria())&&StringUtils.isNotBlank(filter.getCriteria())){
            query.append(" AND (");
            query.append(" lower(x.supplierId) LIKE :CRITERIA ");
            query.append(" OR lower(x.company) LIKE :CRITERIA ");
            query.append(" OR lower(x.state) LIKE :CRITERIA ");
            query.append(" OR lower(x.country) LIKE :CRITERIA ");
            query.append(") ");
        }
        return query.toString();
    }

}

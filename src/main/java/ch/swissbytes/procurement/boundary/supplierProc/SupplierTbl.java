package ch.swissbytes.procurement.boundary.supplierProc;


import ch.swissbytes.Service.business.supplierProc.SupplierProcDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/18/2015.
 */
public class SupplierTbl extends LazyDataModel<SupplierProcEntity> {

    private static final Logger log = Logger.getLogger(SupplierTbl.class.getName());
    private SupplierProcDao dao;
    private Filter filter;

    private Long total;
    private SupplierManagerTable managerTable;

    public SupplierTbl(SupplierProcDao dao, Filter filter) {
        log.info("creating tbl ");
        this.filter = filter;
        this.dao = dao;
        total = 0L;
    }

    public Long getTotal() {
        return total;
    }

    @Override
    public List<SupplierProcEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<SupplierProcEntity> list = new ArrayList<>();
        String field=StringUtils.isEmpty(sortField)?"supplierId":sortField;
        list = dao.findByPage(first, pageSize, filter,field, sortOrder.ordinal() == SortOrder.ASCENDING.ordinal());


        if (super.getRowCount() <= 0) {
            Long total = dao.findTotal(null);
            this.setRowCount(total.intValue());
            this.total = total;
        }
        if(StringUtils.isNotEmpty(filter.getCriteria())&&StringUtils.isNotBlank(filter.getCriteria())){
            Long total = dao.findTotal(filter);
            this.setRowCount(total.intValue());
            this.total=total;
        }
        this.setPageSize(pageSize);
        return list;
    }
}

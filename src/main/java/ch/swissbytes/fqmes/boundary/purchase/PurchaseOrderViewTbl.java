package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.Service.business.purchase.PurchaseOrderViewDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.VPurchaseOrder;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
public class PurchaseOrderViewTbl extends LazyDataModel<VPurchaseOrder> {

    private static final Logger log = Logger.getLogger(PurchaseOrderViewTbl.class.getName());
    private PurchaseOrderViewDao dao;
    private Filter filter;

    private Long total;

    public PurchaseOrderViewTbl(PurchaseOrderViewDao dao, Filter filter) {
        log.info("creating tbl ");
        log.info((dao != null ? "dao has value" : "dao has no value"));
        this.filter = filter;
        this.dao = dao;
        total = 0L;
    }

    public Long getTotal() {
        return total;
    }

    @Override
    public List<VPurchaseOrder> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<VPurchaseOrder> purchaseOrderEntityList = new ArrayList<>();
        if (sortField != null) {
            purchaseOrderEntityList = dao.findByPage(first, pageSize, filter, sortField, sortOrder.ordinal() == SortOrder.ASCENDING.ordinal());
        } else {
            purchaseOrderEntityList = dao.findByPage(first, pageSize, filter);
        }
        if (super.getRowCount() <= 0) {
            Long total = dao.findTotal(null);
            this.setRowCount(total.intValue());
            this.total = total;
        }
        this.setPageSize(pageSize);
        if (sortField != null && sortField.equals("po")) {
            Collections.sort(purchaseOrderEntityList);
            if (sortOrder.ordinal() == SortOrder.DESCENDING.ordinal()) {
                Collections.reverse(purchaseOrderEntityList);
            }
        }
        return purchaseOrderEntityList;
    }

    public List<VPurchaseOrder> getAllFiltered() {
        return dao.findByPage(0, getTotal().intValue(), filter);
    }

}

package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.dao.PurchaseOrderDao;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
public class PurchaseOrderTbl extends LazyDataModel<PurchaseOrderEntity> {

    private static final Logger log = Logger.getLogger(PurchaseOrderTbl.class.getName());
    private PurchaseOrderDao dao;
    private Filter filter;

    public PurchaseOrderTbl(PurchaseOrderDao dao,Filter filter){
        log.info("creating tbl ");
        log.info((dao!=null?"dao has value":"dao doesnt have value"));
        this.filter=filter;
        this.dao=dao;
    }

    @Override
    public List<PurchaseOrderEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        log.info("loading data...");
        List<PurchaseOrderEntity> purchaseOrderEntityList= dao.findByPage(first, pageSize,filter);
        if(super.getRowCount()<=0){
            Long total= dao.findTotal(null);
            this.setRowCount(total.intValue());
        }
        this.setPageSize(pageSize);
        return purchaseOrderEntityList;
    }

}

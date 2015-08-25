package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.business.purchase.PurchaseOrderDao;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.ArrayList;
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

    private Long total;

    public PurchaseOrderTbl(PurchaseOrderDao dao,Filter filter){
        log.info("creating tbl ");
        log.info((dao!=null?"dao has value":"dao has no value"));
        this.filter=filter;
        this.dao=dao;
        total=0L;
    }

    public Long getTotal() {
        return total;
    }

    @Override
    public List<PurchaseOrderEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        log.info("loading data... "+sortField);
        List<PurchaseOrderEntity> purchaseOrderEntityList=new ArrayList<>();
        if(sortField!=null){
            purchaseOrderEntityList= dao.findByPage(first, pageSize,filter,sortField,sortOrder.ordinal()==SortOrder.ASCENDING.ordinal());
        }else{
            purchaseOrderEntityList= dao.findByPage(first, pageSize,filter);
        }
        if(super.getRowCount()<=0){
            Long total= dao.findTotal(filter);
            this.setRowCount(total.intValue());
            this.total=total;
        }
        this.setPageSize(pageSize);
        return purchaseOrderEntityList;
    }

}

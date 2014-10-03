package ch.swissbytes.fqmes.report;

import ch.swissbytes.fqmes.boundary.purchase.PurchaseOrderTbl;
import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.fqmes.model.dao.PurchaseOrderDao;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class ReportListBean implements Serializable {

    @Inject
    private PurchaseOrderDao dao;

    private PurchaseOrderTbl tbl;

    private SearchPurchase searchPurchase;

    private static final Logger log = Logger.getLogger(ReportListBean.class.getName());


    @PostConstruct
    public void create(){
        log.info("creating bean purchase list");
        log.log(Level.FINER,"FINER log");
        tbl=new PurchaseOrderTbl(dao,searchPurchase);
        searchPurchase=new SearchPurchase();
    }

    private void  search(){
        log.info("search..."+searchPurchase);
        tbl= new PurchaseOrderTbl(dao,searchPurchase);
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying bean");
    }

    public LazyDataModel<PurchaseOrderEntity> getList(){
        return tbl;
    }

    public void doSearch(){
        log.info("doSearch");
        search();
    }
    public void doClean(){
        searchPurchase.clean();
        search();
    }
    public SearchPurchase getSearchPurchase() {
        return searchPurchase;
    }
}

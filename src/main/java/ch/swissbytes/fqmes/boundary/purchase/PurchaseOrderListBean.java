package ch.swissbytes.fqmes.boundary.purchase;

import ch.swissbytes.fqmes.control.purchase.PurchaseOrderService;
import ch.swissbytes.fqmes.model.dao.PurchaseOrderViewDao;
import ch.swissbytes.domain.repository.entities.PurchaseOrderEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
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
public class PurchaseOrderListBean implements Serializable {

    @Inject
    private PurchaseOrderViewDao dao;

    @Inject
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrderTbl tbl;

    private PurchaseOrderViewTbl vTbl;

    private SearchPurchase searchPurchase;

    private Long purchaseOrderId;

    private PurchaseOrderEntity purchaseOrderSelected;

    private static final Logger log = Logger.getLogger(PurchaseOrderListBean.class.getName());


    @PostConstruct
    public void create(){
        log.info("creating bean purchase list");
        log.log(Level.FINER, "FINER log");
        //tbl=new PurchaseOrderTbl(dao,searchPurchase);
        vTbl = new PurchaseOrderViewTbl(dao,searchPurchase);
        searchPurchase=new SearchPurchase();
    }

    private void  search(){
        log.info("search..."+searchPurchase);
        //tbl= new PurchaseOrderTbl(dao,searchPurchase);
        vTbl = new PurchaseOrderViewTbl(dao,searchPurchase);
    }

    public void selectPurchaseOrderId(final Long purchaseOrderId){
        log.info("selectPurchaseOrderId(purchaseOrderId["+purchaseOrderId+"])");
        this.purchaseOrderId = purchaseOrderId;
        purchaseOrderSelected = dao.findById(PurchaseOrderEntity.class,purchaseOrderId).get(0);
        log.info("Purchase Selected: " + purchaseOrderSelected.getProject());
    }

    public void selectPurchaseOrder(){
        log.info("selectPurchaseOrder()");
        log.info("purchaseOrderId: " + purchaseOrderId);
        purchaseOrderSelected = dao.findById(PurchaseOrderEntity.class,purchaseOrderId).get(0);
        log.info("Purchase Selected: " + purchaseOrderSelected.getProject());
    }

    public void doDeletePurchaseOrder(){
        purchaseOrderService.doDelete(purchaseOrderId);
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying bean");
    }

    public PurchaseOrderTbl getList(){
        return tbl;
    }

    public PurchaseOrderViewTbl getListView() {
        return vTbl;
    }

    public void doSearch(){
        log.info("doSearch");
        search();
    }
    public void doClean(){
        searchPurchase.clean();
        search();
    }

    @Produces
    @Named
    @RequestScoped
    public SearchPurchase getSearchPurchase() {
        return searchPurchase;
    }

    public PurchaseOrderEntity getPurchaseOrderSelected() {
        return purchaseOrderSelected;
    }

    public void setPurchaseOrderSelected(PurchaseOrderEntity purchaseOrderSelected) {
        this.purchaseOrderSelected = purchaseOrderSelected;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        log.info("setPurchaseOrderId(Long purchaseOrderId["+purchaseOrderId+"])");
        this.purchaseOrderId = purchaseOrderId;
        purchaseOrderSelected = dao.findById(PurchaseOrderEntity.class,purchaseOrderId).get(0);
        log.info("Purchase Selected: " + purchaseOrderSelected.getProject());
    }

    public String redirectCreatePurchaseOrder(){
        return "/purchase/create?faces-redirect=true";
    }
}

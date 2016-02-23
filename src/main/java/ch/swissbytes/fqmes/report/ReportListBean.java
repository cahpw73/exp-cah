package ch.swissbytes.fqmes.report;

import ch.swissbytes.Service.business.purchase.PurchaseOrderViewDao;
import ch.swissbytes.fqmes.boundary.purchase.PurchaseOrderViewTbl;
import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;

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
    private PurchaseOrderViewDao dao;


    private PurchaseOrderViewTbl tbl;

    private SearchPurchase searchPurchase = new SearchPurchase();

    private static final Logger log = Logger.getLogger(ReportListBean.class.getName());


    @PostConstruct
    public void create() {
        log.info("creating bean purchase list");
        log.log(Level.FINER, "FINER log");
        tbl = new PurchaseOrderViewTbl(dao, searchPurchase);

    }

    private void search() {
        log.info("search...");
        fixedStatusesTerms();
        tbl = new PurchaseOrderViewTbl(dao, searchPurchase);
    }

    private void fixedStatusesTerms() {
        if (searchPurchase.getStatuses()!=null && searchPurchase.getStatuses().length() > 1) {
            searchPurchase.setStatuses(searchPurchase.getStatuses().replaceAll("\\s", ""));
            String status1 = searchPurchase.getStatuses().substring(0, 1);
            try {
                Integer i = Integer.parseInt(status1);
            } catch (NumberFormatException nfe) {
                searchPurchase.setStatuses(searchPurchase.getStatuses().substring(1, searchPurchase.getStatuses().length()));
            }
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying bean");
    }

    public PurchaseOrderViewTbl getList() {
        return tbl;
    }

    public void doSearch() {
        log.info("doSearch");
        search();
    }

    public void doClean() {
        searchPurchase.clean();
        search();
    }

    public SearchPurchase getSearchPurchase() {
        return searchPurchase;
    }

}

package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcDao;
import ch.swissbytes.Service.infrastructure.Filter;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import java.util.logging.Logger;

/**
 * Created by alvaro on 5/18/2015.
 */
@Named
@ViewScoped
public class SupplierProcList implements Serializable {

    private static final Logger log = Logger.getLogger(SupplierProcList.class.getName());

    private Filter filter=new Filter();
    private SupplierTbl list;
    @Inject
    private SupplierProcDao dao;

    @PostConstruct
    public void create(){
        log.info("SupplierProcList bean created");
        //filter=new Filter();
        list=new SupplierTbl(dao,filter);
    }

    @PreDestroy
    public void destroy(){
        log.info("SupplierProcList bean destroyed");
    }

    public void doSearch(){
        list=new SupplierTbl(dao,filter);
    }

    public Filter getFilter() {
        return filter;
    }

    public SupplierTbl getList() {
        return list;
    }
}

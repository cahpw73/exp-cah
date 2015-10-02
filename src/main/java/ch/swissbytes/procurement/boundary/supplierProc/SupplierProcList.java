package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcDao;
import ch.swissbytes.Service.business.supplierProc.SupplierProcService;

import ch.swissbytes.domain.model.entities.SupplierProcEntity;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.SortEvent;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.io.Serializable;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/18/2015.
 */
@Named
@ViewScoped
public class SupplierProcList implements Serializable {

    private static final Logger log = Logger.getLogger(SupplierProcList.class.getName());

    @Inject
    private SupplierProcDao dao;
    @Inject
    private SupplierProcService service;
    private SupplierTbl list;
    private List<SupplierProcEntity> suppliers;
    @Inject
    private SupplierManagerTable managerTable;

    @PostConstruct
    public void create() {
        log.info("SupplierProcList bean created");
        if (!managerTable.getRemember()) {
            managerTable.clear();
        }
        list = new SupplierTbl(dao, managerTable.getFilter());
    }

    public List<SupplierProcEntity> getSuppliers() {
        if (suppliers == null || suppliers.size() == 0) {
            suppliers = service.findAll();
        }
        return suppliers;
    }

    public void updateSupplierList() {
        suppliers = service.findAll();
    }

    @PreDestroy
    public void destroy() {
        log.info("SupplierProcList bean destroyed");
    }

    public void doSearch() {
        managerTable.clear();
        list = new SupplierTbl(dao, managerTable.getFilter());
    }

    public SupplierTbl getList() {
        return list;
    }

    public DataTable getDataTable() {
        return managerTable.getDataTable();
    }

    public void setDataTable(DataTable datatable) {
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        if (managerTable.isSortInitialized()) {
            log.info("sorting by "+managerTable.getSortBy());
            datatable.setValueExpression("sortBy", expressionFactory.createValueExpression(elContext, managerTable.getSortBy(), Object.class));
            datatable.setSortOrder(managerTable.getDirection());
        }
        managerTable.setDataTable(datatable);
    }

    public void sortListener(SortEvent event) {
        managerTable.setSortBy(event.getSortColumn().getValueExpression("sortBy").getExpressionString());
        managerTable.setDirection(event.isAscending() ? "ascending" : "descending");
        managerTable.setSortInitialized(true);
    }
}

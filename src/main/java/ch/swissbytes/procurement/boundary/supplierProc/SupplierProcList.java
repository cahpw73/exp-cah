package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcDao;
import ch.swissbytes.Service.business.supplierProc.SupplierProcService;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;

import org.apache.commons.lang.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.SortEvent;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
    @Inject
    private SupplierManagerTable managerTable;

    private SupplierTbl list;
    private List<SupplierProcEntity> suppliers;
    private Filter filter;
    private String criteria;
    private String scrollTop = "0";
    private Long supplierId;
    private SupplierProcEntity currentSupplier;

    @PostConstruct
    public void create() {
        log.info("SupplierProcList bean created");
        if (!managerTable.getRemember()) {
            managerTable.clear();
        }
        filter = new Filter();
        filter.setCriteria("");
        list = new SupplierTbl(dao, filter);

    }

    public void load() {
        if (criteria != null) {
            filter.setCriteria(criteria);
        }
        list = new SupplierTbl(dao, filter);
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
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
        list = new SupplierTbl(dao, filter);
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
            log.info("sorting by " + managerTable.getSortBy());
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

    public String redirectToEdit() {
        log.info("redirectToEdit");
        String criteriaFilter = StringUtils.isNotEmpty(filter.getCriteria()) ? filter.getCriteria() : "";
        log.info("supplierId=\" + ["+supplierId+"] + \"&FILTER=\" + ["+criteriaFilter+"] + \"&anchor=\" + ["+scrollTop+"];");
        String url="edit.xhtml?faces-redirect=true&supplierId=" + supplierId + "&FILTER=" + criteriaFilter + "&anchor=" + scrollTop;
        log.info(url);
        return url;
    }

    public void loadSupplierToEdit(SupplierProcEntity supplier){
        log.info("loadSupplierToEdit(SupplierProcEntity supplier ["+(supplier.getActive()!=null?supplier.getActive():false)+"])");
        currentSupplier = supplier;
        log.info("After changes supplier ["+(currentSupplier.getActive()!=null?currentSupplier.getActive():false)+"])");
    }

    public String onlyExit(){
        log.info("onlyExit");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('confSaveActiveSupplierDlg').hide();");
        currentSupplier = null;
        load();
        criteria = StringUtils.isNotEmpty(criteria)?criteria:"";
        return "list?faces-redirect=true&FILTER="+criteria+"&anchor="+scrollTop;
    }

    public String exitAndSave(){
        log.info("exitAndSave");
        service.doUpdate(currentSupplier);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('confSaveActiveSupplierDlg').hide();");
        currentSupplier = null;
        load();
        criteria = StringUtils.isNotEmpty(criteria)?criteria:"";
        return "list?faces-redirect=true&FILTER="+criteria+"&anchor="+scrollTop;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(String scrollTop) {
        this.scrollTop = scrollTop;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
}

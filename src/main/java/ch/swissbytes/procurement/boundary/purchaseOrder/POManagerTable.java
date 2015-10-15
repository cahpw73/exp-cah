package ch.swissbytes.procurement.boundary.purchaseOrder;


import ch.swissbytes.procurement.boundary.ManagerTable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Created by alvaro on 10/15/2015.
 */
@Named
@SessionScoped
public class POManagerTable extends ManagerTable {

    private FilterPO filter;

    @PostConstruct
    public void create(){
        filter=new FilterPO();
        filter.setPOOO();
        this.setFilter(filter);
    }

    @Override
    public Integer findCurrentPage(){
        return 0;
    }
}

package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.fqm.boundary.TableCommonFeature;
import ch.swissbytes.procurement.boundary.ManagerTable;
import org.primefaces.event.data.PageEvent;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by alvaro on 9/22/2015.
 */
@Named
@SessionScoped
public class SupplierManagerTable extends ManagerTable {

    @Inject
    private TableCommonFeature defaultSize;

    @Override
    protected Integer findCurrentPage() {
        return 1;
    }

    @Override
    public void onPaginate(PageEvent event) {
        super.onPaginate(event);
        defaultSize.setDefaultPageSize(this.getDefaultPageSize());
    }


}

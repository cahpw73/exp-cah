package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.fqm.boundary.TableCommonFeature;
import ch.swissbytes.procurement.boundary.ManagerTable;
import org.primefaces.event.data.PageEvent;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;

/**
 * Created by alvaro on 9/22/2015.
 */
@Named
@SessionScoped
public class SupplierManagerTable extends ManagerTable {

    @Inject
    private TableCommonFeature defaultSize;

    @Inject
    private SupplierProcService service;

    @Override
    public Integer findCurrentPage() {
        BigInteger supplierId = BigInteger.valueOf(lastEdited);
        if(lastEdited > 0L) {
            return getCurrentPage(); //service.findPageByCurrentSupplier(supplierId, filter.getCriteria(), defaultSize.getDefaultPageSize());
        }
        return 0;
    }

    @Override
    public void onPaginate(PageEvent event) {
        super.onPaginate(event);
        defaultSize.setDefaultPageSize(this.getDefaultPageSize());
    }


}

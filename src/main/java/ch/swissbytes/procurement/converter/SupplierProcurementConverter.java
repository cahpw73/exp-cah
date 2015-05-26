package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.LogoEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/01/15.
 */
@FacesConverter("supplierProcurementConv")
public class SupplierProcurementConverter implements Converter {

    public static final Logger log = Logger.getLogger(SupplierProcurementConverter.class.getName());

    @Inject
    private SupplierProcService supplierProcService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        try {
            Long.parseLong(value);
            Long supplierProcId = Long.valueOf(value);
            SupplierProcEntity supplier = supplierProcService.findById(supplierProcId);
            return supplier;
        }catch (NumberFormatException nfe){
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof SupplierProcEntity) {
            string = ((SupplierProcEntity) value).getId().toString();
        }
        log.info("vale returned "+string);
        return string;
    }
}

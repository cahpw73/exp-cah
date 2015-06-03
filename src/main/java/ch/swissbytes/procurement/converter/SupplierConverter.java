package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/01/15.
 */
@FacesConverter("supplierConverter")
public class SupplierConverter implements Converter {

    public static final Logger log = Logger.getLogger(SupplierConverter.class.getName());

    @Inject
    private SupplierProcService service;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        try {
            Long id = Long.parseLong(value);
            SupplierProcEntity supplier=service.findById(id);
            return supplier;
        } catch (NumberFormatException nfe) {
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
        return string;
    }
}

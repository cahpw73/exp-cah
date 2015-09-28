package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.procurement.boundary.supplierProc.CategoryBrandBean;

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
@FacesConverter("brandConverter")
public class BrandConverter implements Converter {

    public static final Logger log = Logger.getLogger(BrandConverter.class.getName());

    @Inject
    private BrandDao brandDao;

    @Inject
    private CategoryBrandBean categoryBrandBean;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        System.out.println("brand...... getAsObject.....");
        return  categoryBrandBean.findBrandByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof BrandEntity) {
            string = ((BrandEntity) value).getName();
        }
        return string;
    }
}

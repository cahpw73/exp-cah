package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.category.CategoryDao;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.procurement.boundary.supplierProc.CategoryBrandBean;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/01/15.
 */
@FacesConverter("categoryConverter")
public class CategoryConverter implements Converter {

    public static final Logger log = Logger.getLogger(CategoryConverter.class.getName());

    @Inject
    private CategoryDao categoryDao;

    @Inject
    private CategoryBrandBean categoryBrandBean;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        return categoryBrandBean.findCategoryByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof CategoryEntity) {
            string = ((CategoryEntity) value).getName();
        }
        return string;
    }
}

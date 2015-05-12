package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.domain.model.entities.BrandEntity;

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

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        List<BrandEntity> list = brandDao.findByName(value);
        BrandEntity brand = null;
        if(!list.isEmpty()){
            brand = brandDao.findByName(value).get(0);
        }
        return brand;
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

package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.LogoEntity;

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
@FacesConverter("logoConverter")
public class LogoConverter implements Converter {

    public static final Logger log = Logger.getLogger(LogoConverter.class.getName());

    @Inject
    private LogoService logoService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        try {
            LogoEntity logo = null;
            Long.parseLong(value);
            for (LogoEntity logoEntity : logoService.getLogoList()) {
                if (logoEntity.getId().intValue() == Integer.parseInt(value)) {
                    logo = logoEntity;
                    break;
                }
            }
            return logo;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof LogoEntity) {
            string = ((LogoEntity) value).getId().toString();
        }
        return string;
    }
}

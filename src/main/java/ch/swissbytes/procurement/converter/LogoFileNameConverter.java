package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.logo.LogoService;
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
@FacesConverter("logoFileNameConverter")
public class LogoFileNameConverter implements Converter {

    public static final Logger log = Logger.getLogger(LogoFileNameConverter.class.getName());

    @Inject
    private LogoService logoService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        List<LogoEntity> list = logoService.findByFileName(value);
        LogoEntity logoEntity = null;
        if(!list.isEmpty()){
            logoEntity = list.get(0);
        }
        return logoEntity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof LogoEntity) {
            string = ((LogoEntity) value).getFileName();
        }
        log.info("vale returned "+string);
        return string;
    }
}

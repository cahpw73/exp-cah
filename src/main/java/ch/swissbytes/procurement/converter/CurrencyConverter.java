package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.currency.CurrencyService;
import ch.swissbytes.domain.model.entities.CurrencyEntity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/01/15.
 */
@FacesConverter("currencyConverter")
public class CurrencyConverter implements Converter {

    public static final Logger log = Logger.getLogger(CurrencyConverter.class.getName());

    @Inject
    private CurrencyService currencyService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        try {
            Long currencyId = Long.parseLong(value);
            CurrencyEntity currency = currencyService.findById(currencyId);
            return currency;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof CurrencyEntity) {
            string = ((CurrencyEntity) value).getId().toString();
        }
        return string;
    }
}

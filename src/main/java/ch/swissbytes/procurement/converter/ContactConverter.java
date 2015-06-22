package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.contact.ContactService;
import ch.swissbytes.domain.model.entities.ContactEntity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/01/15.
 */
@FacesConverter("contactConverter")
public class ContactConverter implements Converter {

    public static final Logger log = Logger.getLogger(ContactConverter.class.getName());



    @Inject
    private ContactService contactService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        try {
            Long id = Long.parseLong(value);
            ContactEntity contactEntity= contactService.findById( id);
            System.out.println(contactEntity);
            return contactEntity;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        System.out.println("value "+value);
        if (value instanceof ContactEntity) {
            string = ((ContactEntity) value).getId().toString();

        }
        System.out.println("value returned "+string);
        return string;
    }
}

package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/01/15.
 */
@FacesConverter("mainDocumentConverter")
public class MainDocumentConverter implements Converter {

    public static final Logger log = Logger.getLogger(MainDocumentConverter.class.getName());

    @Inject
    private MainDocumentService service;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        MainDocumentEntity entity = null;
        for (MainDocumentEntity ts : service.getMainDocumentList()) {
            if (ts.getId().intValue() == Integer.parseInt(value)) {
                entity = ts;
                break;
            }
        }
        return entity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof MainDocumentEntity) {
            string = ((MainDocumentEntity) value).getId().toString();
        }
        return string;
    }
}

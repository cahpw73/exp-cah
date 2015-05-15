package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;

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
@FacesConverter("textSnippetConverter")
public class TextSnippetConverter implements Converter {

    public static final Logger log = Logger.getLogger(TextSnippetConverter.class.getName());

    @Inject
    private TextSnippetService service;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        System.out.println("value " + value);
        if (value == null) {
            return null;
        }
       // List<TextSnippetEntity> list = service.findByText(value);
        TextSnippetEntity textSnippetEntity = null;
        for (TextSnippetEntity ts : service.getTextSnippetList()) {
            System.out.println("comparing " + ts.getId() + "  " + value);
            if (ts.getId().intValue() == Integer.parseInt(value)) {
                textSnippetEntity = ts;
                break;
            }
        }
        System.out.println("returning " + textSnippetEntity!=null?textSnippetEntity.getTextSnippet():"nothing found");
        return textSnippetEntity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof TextSnippetEntity) {
            string = ((TextSnippetEntity) value).getId().toString();
        }
        System.out.println("getasstring "+string);
        return string;
    }
}

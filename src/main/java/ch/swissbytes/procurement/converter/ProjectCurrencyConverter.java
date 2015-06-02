package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.currency.CurrencyService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectCurrency.ProjectCurrencyService;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;

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
@FacesConverter("projectCurrencyConverter")
public class ProjectCurrencyConverter implements Converter {

    public static final Logger log = Logger.getLogger(ProjectCurrencyConverter.class.getName());

    @Inject
    private ProjectCurrencyService service;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        try {
            Long id = Long.parseLong(value);
            List<ProjectCurrencyEntity> list=service.findByProjectId(id);
            return list.isEmpty()?null:list.get(0);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof ProjectCurrencyEntity) {
            string = ((ProjectCurrencyEntity) value).getId().toString();
        }
        return string;
    }
}

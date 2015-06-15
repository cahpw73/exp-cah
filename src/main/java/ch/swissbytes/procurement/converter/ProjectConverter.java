package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.Service.business.project.ProjectDao;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;

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
@FacesConverter("projectConverter")
public class ProjectConverter implements Converter {

    public static final Logger log = Logger.getLogger(ProjectConverter.class.getName());

    @Inject
    private ProjectService projectService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        try {
            Long projectId = Long.parseLong(value);
            ProjectEntity project = projectService.findProjectById(projectId);
            return project;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof ProjectEntity) {
            string = ((ProjectEntity) value).getId().toString();
        }
        return string;
    }
}

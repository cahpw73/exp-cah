package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.ProjectEntity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/01/15.
 */
@FacesConverter("projectConverterForReport")
public class ProjectConverterForReport implements Converter {

    public static final Logger log = Logger.getLogger(ProjectConverterForReport.class.getName());

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
            log.info("projectId= "+projectId);
            if(projectId.longValue()>0) {
                ProjectEntity project = projectService.findProjectById(projectId);
                return project;
            }else{
                ProjectEntity project = new ProjectEntity();
                project.setId(-1L);
                return project;
            }
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

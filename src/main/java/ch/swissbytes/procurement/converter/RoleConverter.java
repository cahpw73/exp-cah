package ch.swissbytes.procurement.converter;

import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.Service.business.role.RoleDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.types.RoleEnum;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 18/05/15.
 */
@FacesConverter("roleConverter")
public class RoleConverter implements Converter {

    public static final Logger log = Logger.getLogger(RoleConverter.class.getName());

    @Inject
    private RoleDao roleDao;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (value == null) {
            return null;
        }
        List<RoleEntity> list = roleDao.findByName(value);
        RoleEntity roleEntity = null;
        if(!list.isEmpty()){
            roleEntity = list.get(0);
        }
        return roleEntity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String string = null;
        if (value instanceof RoleEntity) {
            string = ((RoleEntity) value).getName();
        }
        return string;
    }
}

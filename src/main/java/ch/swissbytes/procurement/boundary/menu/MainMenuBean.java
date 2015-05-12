package ch.swissbytes.procurement.boundary.menu;

import ch.swissbytes.Service.business.brand.BrandService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.types.MenuProcurementEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@RequestScoped
public class MainMenuBean implements Serializable {

    private static final Logger log = Logger.getLogger(MainMenuBean.class.getName());

    private MenuProcurementEnum selected=MenuProcurementEnum.PROJECT;

    @PostConstruct
    public void create(){
        log.info("created MainMenuBean");
    }

    public void select(Integer menu){
        selected=MenuProcurementEnum.getEnum(menu);
    }

    public boolean isSelected(Integer menu){
        return MenuProcurementEnum.getEnum(menu).ordinal()==selected.ordinal();
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying MainMenuBean");
    }


}

package ch.swissbytes.procurement.boundary.logo;

import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.domain.model.entities.LogoEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
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
public class LogoBean implements Serializable {

    private static final Logger log = Logger.getLogger(LogoBean.class.getName());

    private LogoEntity logo;

    private LogoEntity logoSelected;

    private List<LogoEntity> logos;

    @Inject
    private LogoService service;

    @PostConstruct
    public void create(){
        log.info("created LogoBean");
        initialize();
    }

    private void initialize(){
        logo=new LogoEntity();
        logos= service.getLogoList();

    }


    public void doSave(){
        logo.setDescription("new file "+new Date().getTime());
        service.doSave(logo);
        log.info(String.format("logo has been saved [%s]",logo.getDescription()));
    }

    public void doDelete(){
        log.info(String.format("logo has been removed [%s]",logo.getDescription()));
    }

    public LogoEntity getLogo() {
        return logo;
    }

    public void setLogo(LogoEntity logo) {
        this.logo = logo;
    }

    public LogoEntity getLogoSelected() {
        return logoSelected;
    }

    public void setLogoSelected(LogoEntity logoSelected) {
        this.logoSelected = logoSelected;
    }

    public List<LogoEntity> getLogos() {
        return logos;
    }

    public void setLogos(List<LogoEntity> logos) {
        this.logos = logos;
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying LogoBean");
    }

}

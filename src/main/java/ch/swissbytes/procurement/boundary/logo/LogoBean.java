package ch.swissbytes.procurement.boundary.logo;

import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.domain.model.entities.LogoEntity;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class LogoBean implements Serializable {

    private static final Logger log = Logger.getLogger(LogoBean.class.getName());

    private LogoEntity logo;

    private LogoEntity logoSelected;

    private Long selected;

    private List<LogoEntity> logos;

    @Inject
    private LogoService service;

    @PostConstruct
    public void create() {
        log.info("created LogoBean");
        initialize();
    }

    private void initialize() {
        logo = new LogoEntity();
        logos = service.getLogoList();
        log.info("size " + logos.size());
    }


    public void restart(){
        logo=new LogoEntity();
    }

    public void handleUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        new Util().enterFile(uf, logo);
        //log.info("logo.data "+logo.getFile());
    }

    public String doSave() {
        log.info("trying to save new logo");
        //logo.setDescription("new file " + new Date().getTime());
        if(!validate()){
            return "";
        }

        service.save(logo);
        log.info(String.format("logo has been saved [%s]", logo.getDescription()));
        return "logo?faces-redirect=true";
    }

    public void saveForProject(){
        if(!validate()){

        }else {
            service.save(logo);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('logoModal').hide();");
        }
    }
    private boolean validate(){
        boolean valid=true;
        if(logo.getFile()==null){
            Messages.addFlashError("graphicId","Please choose an image");
            valid=false;
        }
        if(StringUtils.isEmpty(logo.getDescription())&&StringUtils.isBlank(logo.getDescription())){
            Messages.addFlashError("description","Please enter description");
            valid=false;
        }
        return valid;

    }

    public String doDelete() {
        log.info("removing component");
        if(logoSelected!=null){
            service.delete(logoSelected);
            log.info(String.format("logo has been removed [%s]", logo.getDescription()));
        }
        return "logo?faces-redirect=true";
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
        log.info("setting logo");
        this.logoSelected = logoSelected;
    }

    public List<LogoEntity> getLogos() {
        log.info("getting size of logos "+logos.size());
        return logos;
    }

    public void setLogos(List<LogoEntity> logos) {
        this.logos = logos;
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying LogoBean");
    }

    public Long getSelected() {
        return selected;
    }

    public void setSelected(Long selected) {
        this.selected = selected;
    }
}

package ch.swissbytes.procurement.boundary.logo;

import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.domain.model.entities.LogoEntity;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Util;
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
        log.info("size "+logos.size());
    }


    public void handleUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        new Util().enterFile(uf,logo);
        //log.info("logo.data "+logo.getFile());
    }

    public String doSave() {
        log.info("trying to save new logo");
        //logo.setDescription("new file " + new Date().getTime());
        logo.setLastUpdate(new Date());
        logo.setStatus(StatusEnum.ENABLE);
        service.doSave(logo);
        log.info(String.format("logo has been saved [%s]", logo.getDescription()));
        return "logo?faces-redirect=true";
    }

    public void doDelete() {
        log.info(String.format("logo has been removed [%s]", logo.getDescription()));
    }

    public StreamedContent getLogoImage(LogoEntity logo){
        log.info("converting to streaming "+logo.getFile());
    /*    InputStream is = new ByteArrayInputStream(logo.getFile());
        log.info("is "+is);
        StreamedContent st=  new DefaultStreamedContent(is);
        log.info("already converted "+st);*/

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            log.info("render response....");
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Get ID value from actual request param.
            //String id = context.getExternalContext().getRequestParameterMap().get("id");
            //Image image = service.find(Long.valueOf(id));
            log.info("requesting image");
            return new DefaultStreamedContent(new ByteArrayInputStream(logo.getFile()));
        }
     //   return st;
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

}

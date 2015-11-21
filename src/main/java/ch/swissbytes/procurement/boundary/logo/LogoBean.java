package ch.swissbytes.procurement.boundary.logo;

import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.LogoEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
@ViewScoped
public class LogoBean implements Serializable {

    private static final Logger log = Logger.getLogger(LogoBean.class.getName());

    @Inject
    private LogoService service;

    @Inject
    private ProjectService projectService;

    @Inject
    private Configuration configuration;

    private LogoEntity logo;

    private LogoEntity logoSelected;

    private Long selected;

    private List<LogoEntity> logos;
    private List<LogoEntity>allLogos;

    private LogoEntity logoEdited;

    @PostConstruct
    public void create() {
        log.info("created LogoBean");
        initialize();
    }

    private void initialize() {
        logo = new LogoEntity();
        logos = service.getLogoList();
        log.info("size " + logos.size());
        allLogos=service.findAll();
    }

    public void selection(){
        logoEdited=null;
        if(logoSelected!=null) {
            logoEdited = service.findById(logoSelected.getId());
        }
    }

    public List<LogoEntity> getAllLogos() {
        return allLogos;
    }

    public void restart() {
        logo = new LogoEntity();
    }

    public void handleUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        new Util().enterFile(uf, logo);
    }

    public void reloadList() {
        logos = service.getLogoList();
    }

    public String doSave() {
        log.info("trying to save new logo");
        if (!validate()) {
            return "";
        }
        service.save(logo);
        Messages.addFlashGlobalInfo("Logo " + logo.getDescription() + " has been saved");
        log.info(String.format("logo has been saved [%s]", logo.getDescription()));
        return "logo?faces-redirect=true";
    }

    public String doUpdate(){
        logo=logoEdited;
        if(!validate()){
            return "";
        }
        service.doUpdate(logoEdited);
        Messages.addFlashGlobalInfo("Logo "+ logoEdited.getDescription()+ " has been edited");
        return "logo?faces-redirect=true";
    }
    public String doCancel(){
        return "logo?faces-redirect=true";
    }

    public boolean saveForProject() {
        log.info("saving logo for project");
        boolean saved = false;
        if (!validate()) {
        } else {
            logo = service.save(logo);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('logoModal').hide();");
            saved = true;

        }
        return saved;
    }

    private boolean validate() {
        log.info("validating...");
        boolean valid = true;
        if (logo.getFile() == null) {
            Messages.addFlashError("graphicId", "Please choose an image");
            valid = false;
        }
        if (StringUtils.isEmpty(logo.getDescription()) && StringUtils.isBlank(logo.getDescription())) {
            Messages.addFlashError("description", "Please enter description");
            valid = false;
        }
        if(service.existsDescription(logo.getDescription(),logo.getId())){
            valid = false;
            Messages.addFlashError("description", "Description already exists");
        }

        return valid;

    }

    public String doDelete() {
        log.info("removing component");
        logo = service.findById(logoSelected.getId());
        if (logoSelected != null) {
            if (logIsUsedInReports()) {
                Messages.addFlashGlobalError("Can not delete Logo "+ logoSelected.getDescription() + " because it is already being used");
            } else {
                logo.setLastUpdate(Util.convertUTC(new Date(),configuration.getTimeZone()));
                service.delete(logo);
                Messages.addFlashGlobalInfo("Logo "+logo.getDescription()+" was deleted ");
                log.info(String.format("logo has been removed [%s]", logo.getDescription()));
            }
        }
        return "logo?faces-redirect=true";
    }

    private boolean logIsUsedInReports() {
        List<ProjectEntity> list = projectService.findByLogoId(logoSelected.getId());
        boolean result = !list.isEmpty();
        return result;
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
        log.info("getting size of logos " + logos.size());
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

    public LogoEntity getLogoEdited() {
        return logoEdited;
    }

    public void setLogoEdited(LogoEntity logoEdited) {
        this.logoEdited = logoEdited;
    }
}

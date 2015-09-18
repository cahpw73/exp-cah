package ch.swissbytes.procurement.boundary.client;

import ch.swissbytes.Service.business.client.ClientService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.ClientEntity;
import ch.swissbytes.procurement.boundary.Bean;
import ch.swissbytes.procurement.boundary.logo.LogoBean;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by alvaro on 6/30/2015.
 */
@Named
@ViewScoped
public class ClientBean extends Bean implements Serializable {

    @Inject
    private ClientService service;

    private ClientEntity client;
    private String idClient;

    @Inject
    private ProjectService projectService;

    @Inject
    private LogoBean logoBean;
    private Integer currentLogo;


    @Override
    public void initialize(){
        client=new ClientEntity();
        client.setShowTitle(true);
    }

    public void load(){
        if(StringUtils.isNotEmpty(idClient)&&StringUtils.isNotBlank(idClient)){
            try{
                client=service.findById(Long.valueOf(idClient));
                if(client==null){
                    throw new IllegalArgumentException("Client does not exist ["+idClient+"]");
                }
                putModeEdition();
            }catch (NumberFormatException nfe){
                throw new IllegalArgumentException("Invalid Id ["+idClient+"]");
            }
        }else {
            putModeCreation();
        }
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public String doSave(){
        client=service.save(client);
        Messages.addFlashGlobalInfo("The client has been saved.");
        return "edit?faces-redirect=true&clientId="+client.getId();
    }
    public String doUpdate(){
        service.update(client);
        Messages.addFlashGlobalInfo("The client has been saved.");
        return "edit?faces-redirect=true&clientId="+client.getId();

    }
    public String saveAndClose(){
        Messages.addFlashGlobalInfo("The client has been saved!");
        service.save(client);
        return "list?faces-redirect=true";

    }
    public String updateAndClose(){
        Messages.addFlashGlobalInfo("The client has been saved!");
        service.update(client);
        return "list?faces-redirect=true";
    }

    public void delete(Long id){
        if(!projectService.isClientBeingUsed(id)) {
            service.delete(id);
        }else{
            Messages.addGlobalError("Client is being used by a project");
        }
    }


    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public void saveLogo(){
        if(logoBean.saveForProject()) {
            logoBean.reloadList();
            switch (currentLogo) {
                case 0://report logo
                    client.setClientLogo(logoBean.getLogo());
                    break;
                case 1: //client logo
                    client.setReportLogo(logoBean.getLogo());
                    break;
                case 2:
                   // client.setClientFooter(logoBean.getLogo());
                    break;
                case 3:
                    client.setDefaultLogo(logoBean.getLogo());
                    break;
                case 4:
                    client.setDefaultFooter(logoBean.getLogo());
                    break;
                case 5:
                    client.setClientLogoLeft(logoBean.getLogo());
                    break;

            }
        }
    }
    public void startClientLogo(){
        currentLogo=0;
        logoBean.restart();
    }
    public void startReportLogo(){
        currentLogo=1;
        logoBean.restart();
    }

    public void startClientLogoLeft(){
        currentLogo=5;
        logoBean.restart();
    }

    public String getTitlePage(){
        return isBeingCreated()?"New Client" : "Edit Client";
    }
}

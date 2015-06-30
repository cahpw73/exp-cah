package ch.swissbytes.procurement.boundary.client;

import ch.swissbytes.Service.business.client.ClientService;
import ch.swissbytes.domain.model.entities.ClientEntity;
import ch.swissbytes.procurement.boundary.Bean;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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


    @Override
    public void initialize(){
        client=new ClientEntity();
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
        return "edit?faces-redirect=true&clientId="+client.getId();
    }
    public String doUpdate(){
        service.update(client);
        return "edit?faces-redirect=true&clientId="+client.getId();

    }
    public String saveAndClose(){
        service.save(client);
        return "list?faces-redirect=true";

    }
    public String updateAndClose(){
        service.update(client);
        return "list?faces-redirect=true";

    }


    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }
}

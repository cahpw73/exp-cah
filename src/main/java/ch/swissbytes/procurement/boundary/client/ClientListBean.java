package ch.swissbytes.procurement.boundary.client;

import ch.swissbytes.Service.business.client.ClientService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.ClientEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 6/29/2015.
 */
@Named
@ViewScoped
public class ClientListBean implements Serializable {

    @Inject
    private ClientService service;



    private String term;

    @PostConstruct
    public void create(){

    }
    @PreDestroy
    public void destroy(){

    }

   public List<ClientEntity> getClients(){
        return service.findAll(term);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}

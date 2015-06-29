package ch.swissbytes.Service.business.client;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.ClientEntity;

import java.io.Serializable;

/**
 * Created by alvaro on 6/29/2015.
 */

public class ClientService extends Service<ClientEntity> implements Serializable {

    /*@Inject
    private ClientDao dao;

    @PostConstruct
    public void create(){
        super.initialize(dao);
    }

    @Inject
    public List<ClientEntity>findAll(String term){
        return dao.findAll(term);
    }*/
}

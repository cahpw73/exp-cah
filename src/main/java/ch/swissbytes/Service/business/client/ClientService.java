package ch.swissbytes.Service.business.client;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.ClientEntity;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 6/29/2015.
 */

public class ClientService extends Service<ClientEntity> implements Serializable {

    @Inject
    private ClientDao dao;

    @PostConstruct
    public void create(){
        super.initialize(dao);
    }


    public List<ClientEntity> findAll(String term){
        return dao.findAll(term);
    }
}

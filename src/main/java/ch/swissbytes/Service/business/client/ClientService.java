package ch.swissbytes.Service.business.client;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.ClientEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
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

    public ClientEntity findById(Long id){
        return dao.findById(id);
    }

    @Override
    @Transactional
    public ClientEntity save(ClientEntity client){
        client.setLastUpdate(new Date());
        client.setStatus(StatusEnum.ENABLE);
        return super.save(client);
    }
    @Override
    @Transactional
    public ClientEntity update(ClientEntity client){
        client.setLastUpdate(new Date());
        return super.update(client);
    }

    @Transactional
    public ClientEntity delete(Long clientId){
        ClientEntity client=findById(clientId);
        client.setLastUpdate(new Date());
        client.setStatus(StatusEnum.DELETED);
        return super.update(client);
    }
}

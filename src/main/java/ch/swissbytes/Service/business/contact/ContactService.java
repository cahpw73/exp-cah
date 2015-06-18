package ch.swissbytes.Service.business.contact;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class ContactService extends Service<ContactEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ContactService.class.getName());

    @Inject
    private ContactDao dao;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }


    @Transactional
    public void doSave(List<ContactEntity> contacts, SupplierProcEntity supplier) {
        dao.doSave(contacts, supplier);
    }
    @Transactional
    public void doSave(ContactEntity contact, SupplierProcEntity supplier) {
        List<ContactEntity>contacts=new ArrayList<>();
        contact.setStatusEnum(StatusEnum.ENABLE);
        contacts.add(contact);
        dao.doSave(contacts,supplier);
    }

    @Transactional
    public void doUpdate(List<ContactEntity> contacts, SupplierProcEntity supplier) {
        dao.doUpdate(contacts, supplier);
    }


    public List<ContactEntity> findByContactsBySupplier(final Long id) {
        return dao.findByContactsBySupplier(id);
    }

    public ContactEntity findById(Long id){
        List<ContactEntity>contactEntities=dao.findById(ContactEntity.class, id);
        return contactEntities.isEmpty()?null:contactEntities.get(0);
    }


}

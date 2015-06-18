package ch.swissbytes.Service.business.contact;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class ContactDao extends GenericDao<ContactEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ContactDao.class.getName());


    public void doSave(List<ContactEntity> contacts, SupplierProcEntity supplier) {
        Date now = new Date();
        for (ContactEntity contactEntity : contacts) {
            contactEntity.setId(null);
            contactEntity.setLastUpdate(now);
            contactEntity.setSupplier(supplier);
            super.save(contactEntity);
        }
    }

    public ContactEntity doSave(ContactEntity contactEntity,SupplierProcEntity supplierProcEntity){
        contactEntity.setLastUpdate(new Date());
        contactEntity.setSupplier(supplierProcEntity);
        super.save(contactEntity);
        return contactEntity;
    }

    public void doUpdate(List<ContactEntity> contacts, SupplierProcEntity supplier) {
        List<ContactEntity> oldContacts = findByContactsBySupplier(supplier.getId());
        Date now = new Date();
        for (ContactEntity contact : oldContacts) {
            if (contacts.indexOf(contact) < 0) {
                contact.setStatusEnum(StatusEnum.DELETED);
                contact.setLastUpdate(now);
                super.update(contact);
            }
        }
        for (ContactEntity contactEntity : contacts) {
            contactEntity.setLastUpdate(now);
            if (contactEntity.getId() == null || contactEntity.getId() < 0) {
                contactEntity.setId(null);
                contactEntity.setStatusEnum(StatusEnum.ENABLE);
                contactEntity.setSupplier(supplier);
                super.save(contactEntity);
            } else {

                super.update(contactEntity);
            }
        }
    }


    public List<ContactEntity> findByContactsBySupplier(final Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT c ");
        sb.append(" FROM ContactEntity c ");
        sb.append(" WHERE c.statusEnum =:ENABLE ");
        sb.append(" AND c.supplier.id = :SUPPLIER_ID ");
        sb.append(" ORDER BY c.surName, c.firstName ");
        Map<String, Object> params = new HashMap<>();
        params.put("SUPPLIER_ID", id);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }
}

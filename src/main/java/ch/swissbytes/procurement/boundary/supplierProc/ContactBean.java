package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.procurement.boundary.Bean;
import org.apache.commons.beanutils.BeanUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 30-05-15.
 */
@Named
@ViewScoped
public class ContactBean extends Bean {

    private static final Logger log = Logger.getLogger(ContactBean.class.getName());

    private List<ContactEntity> contacts;

    private ContactEntity contact;

    private Long currentId;


    @Override
    protected void initialize() {
        contacts = new ArrayList<>();
        contact = new ContactEntity();
    }

    public void start() {
        switch (modeOperationEnum) {
            case NEW:
                contact = new ContactEntity();
                break;
            case UPDATE:
                ContactEntity temporary=new ContactEntity();
                temporary.setId(currentId);
                int index = contacts.indexOf(temporary);
                if(index>=0&&index<contacts.size()) {
                    contact = contacts.get(index);
                }
                break;
        }
    }

    public Long getCurrentId() {
        return currentId;
    }

    public void saveContact() {
        switch (modeOperationEnum) {
            case NEW:
                contact.setId(currentId);
                try {
                    contacts.add(contacts.size() - 1, (ContactEntity) BeanUtils.cloneBean(contact));
                } catch (Exception ex) {
                }
                break;

            case UPDATE:
                int index = contacts.indexOf(contact);
                if (index >= 0) {
                    contacts.set(index, contact);
                } else {
                    log.log(Level.WARNING, "contact editing was not found");
                }
                break;

        }
    }

    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }

    public List<ContactEntity> getContacts() {
        return contacts;
    }


    public ContactEntity getContact() {
        return contact;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
    }


}

package ch.swissbytes.fqmes.control.scopesupply;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.model.dao.AttachmentScopeSupplyDao;
import ch.swissbytes.domain.repository.entities.AttachmentScopeSupply;
import ch.swissbytes.fqmes.util.DownloadFile;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/22/14.
 */
public class AttachmentScopeSupplyService extends Service<AttachmentScopeSupply> implements Serializable{

    @Inject
     private AttachmentScopeSupplyDao dao;


    private static final Logger log = Logger.getLogger(AttachmentScopeSupplyService.class.getName());

    public AttachmentScopeSupplyService() {
        super.initialize(dao);
    }

    public AttachmentScopeSupply load(Long id) {
        return dao.load(id);
    }

    public List<AttachmentScopeSupply> findByScopeSupply(final Long scopeSupplyId){
        return dao.findByScopeSupply(scopeSupplyId);
    }
    public List<AttachmentScopeSupply> findByScopeSupplyLazy(final Long scopeSupplyId){
        return dao.findByScopeSupplyLazy(scopeSupplyId);
    }

    public void download(final Long id){
        new DownloadFile().downloadAttachedFileOnScopeSupplly(load(id));
    }
}

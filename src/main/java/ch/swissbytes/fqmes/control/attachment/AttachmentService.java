package ch.swissbytes.fqmes.control.attachment;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.model.dao.AttachmentDao;
import ch.swissbytes.fqmes.model.entities.AttachmentEntity;
import ch.swissbytes.fqmes.util.DownloadFile;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 9/22/14.
 */
public class AttachmentService extends Service<AttachmentEntity> implements Serializable {

    @Inject
    private AttachmentDao dao;

    @Inject
    private DownloadFile downloadFile;

    public AttachmentService(){
        super.initialize(dao);
    }

    public AttachmentEntity load(final Long id){
        return dao.load(id);
    }

    public List<AttachmentEntity> findByPurchaseOrder(final Long purchaseOrderId){
        return dao.findByPurchaseOrder(purchaseOrderId);

    }

    public void download(final Long id){
        AttachmentEntity at=load(id);
      //  downloadFile.downloadAttachmentFile(at);
    }

}

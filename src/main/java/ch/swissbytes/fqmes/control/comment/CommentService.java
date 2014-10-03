package ch.swissbytes.fqmes.control.comment;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.model.dao.CommentDao;
import ch.swissbytes.fqmes.model.entities.CommentEntity;
import ch.swissbytes.fqmes.util.DownloadFile;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 9/22/14.
 */
public class CommentService extends Service<CommentEntity> implements Serializable {

    @Inject
    private CommentDao dao;

    @Inject
    private DownloadFile downloadFile;

    public CommentService(){
        super.initialize(dao);
    }

    public CommentEntity load(Long id){
        return dao.load(id);
    }

    public List<CommentEntity> findByPurchaseOrder(final Long purchaseOrderId){
        return dao.findByPurchaseOrder(purchaseOrderId);

    }

    public void download(final Long id){
        CommentEntity commentEntity=load(id);
        downloadFile.downloadAttachedFileOnComment(commentEntity);
    }
}

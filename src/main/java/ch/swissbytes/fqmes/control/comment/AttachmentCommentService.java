package ch.swissbytes.fqmes.control.comment;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.model.dao.AttachmentCommentDao;
import ch.swissbytes.domain.repository.entities.AttachmentComment;
import ch.swissbytes.fqmes.util.DownloadFile;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/22/14.
 */
public class AttachmentCommentService extends Service<AttachmentComment> implements Serializable{

    @Inject
     private AttachmentCommentDao dao;

    private static final Logger log = Logger.getLogger(AttachmentCommentService.class.getName());

    public AttachmentCommentService() {
        super.initialize(dao);
    }

    public AttachmentComment load(Long id) {
        return dao.load(id);
    }

    public List<AttachmentComment> findByComment(final Long commentId){
        return dao.findByComment(commentId);
    }
    public List<AttachmentComment> findByCommentLazy(final Long commentId){
        return dao.findByCommentLazy(commentId);
    }

    public void download(final Long id){
       new DownloadFile().downloadAttachedFileOnComment(load(id));
    }
}

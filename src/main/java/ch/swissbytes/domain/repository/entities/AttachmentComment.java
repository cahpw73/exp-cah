package ch.swissbytes.domain.repository.entities;

import ch.swissbytes.domain.repository.interfaces.ManageFile;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by alvaro on 9/10/14.
 */

@Named
@Entity
@Table(name = "ATTACHMENT_COMMENT")
public class AttachmentComment implements Serializable,EntityTbl,ManageFile{

    private Long id;
    private String fileName;
    private String path;
    private String mimeType;
    private CommentEntity comment;
    private StatusEntity status;
    private byte[] file;
    private Date lastUpdate;


    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "ATTACHMENT_COMMENT_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    @Column(name="file_name", nullable=false, length=500)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    @Column(name="path", length=255)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    @Column(name="MIME_TYPE", nullable=false, length=500)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="COMMENT_ID")
    public CommentEntity getComment() {
        return comment;
    }

    public void setComment(CommentEntity comment) {
        this.comment = comment;
    }
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="STATUS_ID")
    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    @Column(name="file_attached", nullable=false, length = 10485760)
    @Basic(fetch = FetchType.LAZY)
    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    @Column(name="LAST_UPDATE", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}

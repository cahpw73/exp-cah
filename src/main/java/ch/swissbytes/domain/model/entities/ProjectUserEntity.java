package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */


import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "project_user")
public class ProjectUserEntity implements Serializable {

    private Long id;
    private ProjectEntity project;
    private UserEntity user;
    private Date created;
    private Date lastUpdate;
    private UserEntity userCreated;
    private UserEntity userLastUpdate;
    private StatusEnum status;


    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "PROJECT_USER_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "last_update", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_created_id", nullable = false)
    public UserEntity getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(UserEntity userCreated) {
        this.userCreated = userCreated;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_last_update_id", nullable = false)
    public UserEntity getUserLastUpdate() {
        return userLastUpdate;
    }

    public void setUserLastUpdate(UserEntity userLastUpdate) {
        this.userLastUpdate = userLastUpdate;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}

package ch.swissbytes.Service.business.projectUser;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ProjectUserEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.fqmes.util.Encode;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by christian on 12/09/14.
 */
public class ProjectUserService implements Serializable {

    @Inject
    private ProjectUserDao dao;

    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private UserService userService;

    @Transactional
    public ProjectUserEntity doSave(ProjectUserEntity entity) {
        dao.doSave(entity);
        return entity;
    }

    @Transactional
    public ProjectUserEntity doUpdate(ProjectUserEntity entity) {
        dao.doUpdate(entity);
        return entity;
    }

    public List<ProjectUserEntity> findByProjectId(final Long id){
        return dao.findByProjectId(id);
    }

    public List<ProjectUserEntity> findByUserId(final Long id){
        return dao.findByUserId(id);
    }

}

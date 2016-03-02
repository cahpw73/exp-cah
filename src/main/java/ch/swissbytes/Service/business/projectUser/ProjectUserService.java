package ch.swissbytes.Service.business.projectUser;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ProjectUserEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.fqmes.util.Encode;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.inject.Inject;
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

    public ProjectUserEntity doSave(ProjectUserEntity entity) {
        final String passwordHashed = Encode.encode(credentials.getPassword());
        UserEntity userEntity = userService.getUserEntity(credentials.getUserId(), passwordHashed);
        entity.setCreated(new Date());
        entity.setLastUpdate(new Date());
        entity.setUserCreated(userEntity);
        entity.setUserLastUpdate(userEntity);
        return entity;
    }

    public ProjectUserEntity doUpdate(ProjectUserEntity entity) {
        final String passwordHashed = Encode.encode(credentials.getPassword());
        UserEntity userEntity = userService.getUserEntity(credentials.getUserId(), passwordHashed);
        entity.setLastUpdate(new Date());
        entity.setUserLastUpdate(userEntity);
        return entity;
    }

    public List<ProjectUserEntity> findByProjectId(final Long id){
        return dao.findByProjectId(id);
    }

    public List<ProjectUserEntity> findByUserId(final Long id){
        return dao.findByUserId(id);
    }

}

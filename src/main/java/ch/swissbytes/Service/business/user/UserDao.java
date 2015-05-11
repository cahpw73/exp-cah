package ch.swissbytes.Service.business.user;


import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.picketlink.idm.IdentityManager;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dahir on 01-07-14.
 */



public class UserDao extends GenericDao implements Serializable {

    @Inject
    private IdentityManager identityManager;

    @PersistenceContext(unitName = "fqmPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    private static final Logger log = Logger.getLogger(UserDao.class.getSimpleName());


    public void doSave(UserEntity user){
        super.save(user);
    }

    public List<UserEntity> findUserByUserName(final String username){
        String inputValue = username !=null ? username : "";
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT u ");
        sb.append(" FROM UserEntity u ");
        sb.append(" WHERE lower(u.username) = :USER_NAME ");
        sb.append(" AND (u.status.id =:ACTIVE ");
        sb.append(" OR u.status.id =:INACTIVE )");
        Map<String, Object> params = new HashMap<>();
        params.put("USER_NAME", inputValue.toLowerCase().trim());
        params.put("ACTIVE", StatusEnum.ENABLE.getId());
        params.put("INACTIVE", StatusEnum.DISABLED.getId());
        return super.findBy(sb.toString(),params);
    }

    public List<UserEntity> findUserByEmail(final String email){
        log.info("findUserByEmail(final String email["+email+"])");
        String inputValue = email !=null ? email : "";
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT u ");
        sb.append(" FROM UserEntity u ");
        sb.append(" WHERE lower(u.email) = :EMAIL ");
        sb.append(" AND (u.status.id =:ACTIVE ");
        sb.append(" OR u.status.id =:INACTIVE )");
        Map<String, Object> params = new HashMap<>();
        params.put("EMAIL", inputValue.toLowerCase().trim());
        params.put("ACTIVE", StatusEnum.ENABLE.getId());
        params.put("INACTIVE", StatusEnum.DISABLED.getId());
        return super.findBy(sb.toString(),params);
    }

    public List<UserEntity> findDuplicityEmail(final String email, final Long id){
        log.info("findUserByEmail(final String email["+email+"])");
        String inputValue = email !=null ? email : "";
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT u ");
        sb.append(" FROM UserEntity u ");
        sb.append(" WHERE lower(u.email) = :EMAIL ");
        sb.append(" AND u.id<>:ID ");
        sb.append(" AND (u.status.id =:ACTIVE ");
        sb.append(" OR u.status.id =:INACTIVE )");
        Map<String, Object> params = new HashMap<>();
        params.put("EMAIL", inputValue.toLowerCase().trim());
        params.put("ACTIVE", StatusEnum.ENABLE.getId());
        params.put("INACTIVE", StatusEnum.DISABLED.getId());
        params.put("ID", id);
        return super.findBy(sb.toString(),params);
    }

    public List<UserEntity> findDuplicityUserName(final String username, final Long id){
        String inputValue = username !=null ? username : "";
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT u ");
        sb.append(" FROM UserEntity u ");
        sb.append(" WHERE lower(u.username) = :USER_NAME ");
        sb.append(" AND u.id<>:ID ");
        sb.append(" AND (u.status.id =:ACTIVE ");
        sb.append(" OR u.status.id =:INACTIVE )");
        Map<String, Object> params = new HashMap<>();
        params.put("USER_NAME", inputValue.toLowerCase().trim());
        params.put("ACTIVE", StatusEnum.ENABLE.getId());
        params.put("INACTIVE", StatusEnum.DISABLED.getId());
        params.put("ID", id);
        return super.findBy(sb.toString(),params);
    }

    public List<UserEntity> getUserList(){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT u ");
        sb.append(" FROM UserEntity u ");

        Query query = entityManager.createQuery(sb.toString());

        return query.getResultList();
    }

    public List<RoleEntity> findRoleById(Integer id){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT r ");
        sb.append(" FROM RoleEntity r ");
        sb.append(" WHERE r.id=:ROLE_ID ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("ROLE_ID", id);
        return query.getResultList();
    }

    public UserEntity getUser(String username, String password) {

        String hql = "SELECT u FROM UserEntity u " +
                "WHERE u.username = :username and u.password = :password and u.status.id=:enabled";
        TypedQuery<UserEntity> query = this.entityManager.createQuery(hql, UserEntity.class);
        query.setParameter("username", username);
        query.setParameter("enabled", StatusEnum.ENABLE.getId());
        query.setParameter("password", password);

        try {
            List<UserEntity> results = query.getResultList();
            UserEntity user = null;
            if (!results.isEmpty()) {
                user = results.get(0);
            }
            return user;
        } catch (NoResultException nre) {
            log.log(Level.SEVERE, nre.getMessage(), nre.getCause());
        }

        return null;
    }

    public UserEntity getUser(String username) {
        String hql = "SELECT u FROM UserEntity u " +
                "WHERE u.username = :username and u.status.id=:enabled";
        TypedQuery<UserEntity> query = this.entityManager.createQuery(hql, UserEntity.class);
        query.setParameter("username", username);
        query.setParameter("enabled", StatusEnum.ENABLE.getId());
        try {
            List<UserEntity> results = query.getResultList();
            UserEntity user = null;
            if (!results.isEmpty()) {
                user = results.get(0);
            }
            return user;
        } catch (NoResultException nre) {
            log.log(Level.SEVERE, nre.getMessage(), nre.getCause());
        }

        return null;
    }

    protected  void applyCriteriaValues(Query query,Filter filter){
        query.setParameter("DELETED",StatusEnum.DELETED.getId());
    }
    protected String getEntity(){
        return UserEntity.class.getSimpleName();
    }

    protected  String addCriteria(Filter filter){
        StringBuilder sb=new StringBuilder();
        sb.append(" AND x.status.id<>:DELETED ");
        return sb.toString();
    }



}

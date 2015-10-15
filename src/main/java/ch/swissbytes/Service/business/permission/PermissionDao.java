package ch.swissbytes.Service.business.permission;


import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Alvaro on 27/09/14.
 */
public class PermissionDao extends GenericDao<PermissionGrantedEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(PermissionDao.class.getName());

    @Override
    protected void applyCriteriaValues(Query query, Filter f) {

    }

    @Override
    protected String getEntity() {
        return PermissionGrantedEntity.class.getSimpleName();
    }

    @Override
    protected String addCriteria(Filter f) {

        return "";
    }

    public List<PermissionGrantedEntity> getPermissionFor(final List<Integer> roles) {
        log.info("Get Permission Granted by roles list ");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT permission ");
        sb.append("FROM PermissionGrantedEntity permission ");
        if (roles != null && !roles.isEmpty()) {
            sb.append("WHERE permission.role.id IN (:ROLES) ");
        }
        Query query = entityManager.createQuery(sb.toString());
        if (roles != null && !roles.isEmpty()) {
            query.setParameter("ROLES", roles);
        }
        return query.getResultList();
    }

    public List<UserPermissionGrantedEntity> getUserPermissionFor(final Long userId){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p");
        sb.append(" FROM UserPermissionGrantedEntity p");
        sb.append(" WHERE p.user.id = :USER_ID ");
        Map<String,Object> map = new HashMap<>();
        map.put("USER_ID",userId);
        return super.findBy(sb.toString(),map);
    }

    public List<RoleEntity> getRolesAssignedBy(final Long userId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT re ");
        sb.append(" FROM UserEntity r, UserRoleEntity ur, RoleEntity re ");
        sb.append(" WHERE r.id = ur.user.id");
        sb.append(" AND ur.user.id = :USER_ID");
        sb.append(" AND ur.role.id = re.id");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("USER_ID", userId);
        return query.getResultList();
    }

    public List<OptionsEntity> findPermissions(List<Integer> roles) {
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p.options ");
        sb.append(" FROM PermissionGrantedEntity p ");
        sb.append(" WHERE p.role.id IN (:LIST) ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("LIST", roles);
        return query.getResultList();
    }

    public List<RoleEntity> getRolesBy(String user, ModuleSystemEnum module) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT role ");
        sb.append(" FROM UserRoleEntity ur ");
        sb.append(" WHERE ur.moduleSystem=:MODULE ");
        sb.append(" AND ( lower(ur.user.username)=:USER_NAME ");
        sb.append(" OR lower(ur.user.email)=:USER_NAME ) ");
        sb.append(" AND ur.user.status.id=:ENABLED ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("USER_NAME", user != null ? user.toLowerCase().trim() : user);
        query.setParameter("MODULE", module);
        query.setParameter("ENABLED", StatusEnum.ENABLE.getId());
        return query.getResultList();
    }

}

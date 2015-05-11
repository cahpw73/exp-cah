package ch.swissbytes.Service.business.permission;


import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.PermissionGrantedEntity;

import ch.swissbytes.domain.model.entities.RoleEntity;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Alvaro on 27/09/14.
 */
public class PermissionDao extends GenericDao<PermissionGrantedEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(PermissionDao.class.getName());

    @Override
    protected  void applyCriteriaValues(Query query,Filter f){

    }
    @Override
    protected String getEntity(){
        return PermissionGrantedEntity.class.getSimpleName();
    }

    @Override
    protected  String addCriteria(Filter f){

        return "";
    }

    public List<PermissionGrantedEntity> getPermissionFor(final List<Integer>roles){
        log.info("public List<PermissionGrantedEntity> getPermissionFor(final List<Integer>roles="+roles+")");
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT permission ");
        sb.append("FROM PermissionGrantedEntity permission ");
        if(roles!=null&&!roles.isEmpty()){
            sb.append("WHERE permission.role.id IN (:ROLES) ");
        }
        Query query=entityManager.createQuery(sb.toString());
        if(roles!=null&&!roles.isEmpty()){
            query.setParameter("ROLES",roles);
        }
        return query.getResultList();
    }

    public List<RoleEntity> getRolesAssignedBy(final Long userId){
        log.info("public List<RoleEntity> getRolesAssignedBy(final Long userId="+userId+")");
        StringBuilder sb =new StringBuilder();
        sb.append("SELECT user.roleEntity ");
        sb.append("FROM UserEntity user ");
        sb.append("WHERE user.id=:USER_ID");
        Query query=entityManager.createQuery(sb.toString());
        query.setParameter("USER_ID",userId);
        return query.getResultList();
    }




}

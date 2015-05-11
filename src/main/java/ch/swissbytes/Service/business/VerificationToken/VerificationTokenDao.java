package ch.swissbytes.Service.business.VerificationToken;


import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.VerificationTokenEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

public class VerificationTokenDao extends GenericDao implements Serializable {

    private static final Logger log = Logger.getLogger(VerificationTokenDao.class.getName());
    private static final int DEFAULT_EXPIRY_TIME_IN_HOURS = 24;

    @Transactional
    public void doSave(VerificationTokenEntity entity){
        super.saveAndFlush(entity);
    }

    @Transactional
    public VerificationTokenEntity generateVerificationToken(UserEntity user){
        VerificationTokenEntity token = new VerificationTokenEntity();
        createEntityToPersist(token,user);
        super.saveAndFlush(token);
        return token;
    }

    private void createEntityToPersist(VerificationTokenEntity entity, UserEntity user){
        entity.setToken(UUID.randomUUID().toString());
        entity.setVerified(false);
        entity.setExpirationDate(calculateExpirationDate(DEFAULT_EXPIRY_TIME_IN_HOURS));
        entity.setUser(user);
    }

    private Date calculateExpirationDate(int expirationTimeInHours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, expirationTimeInHours);
        return cal.getTime();
    }

    @Transactional
    public void updateToken(VerificationTokenEntity tokenEntity) {
      VerificationTokenEntity entity =(VerificationTokenEntity) super.merge(tokenEntity);
        super.saveAndFlush(entity);
    }

    @Transactional
    public List<VerificationTokenEntity>  getActiveVerifyToken(String token){
        log.info("getActiveVerifyToken(String token)");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT t ");
        sb.append(" FROM VerificationTokenEntity t");
        sb.append(" LEFT JOIN t.user u ");
        sb.append(" WHERE t.token= :TOKEN");
        sb.append(" AND (u.status.id =:ACTIVE ");
        sb.append(" OR u.status.id =:INACTIVE )");
        Map<String, Object> params = new HashMap<>();
        params.put("TOKEN",token);
        params.put("ACTIVE", StatusEnum.ENABLE.getId());
        params.put("INACTIVE", StatusEnum.DISABLED.getId());
        return super.findBy(sb.toString(),params);

    }

    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }
}

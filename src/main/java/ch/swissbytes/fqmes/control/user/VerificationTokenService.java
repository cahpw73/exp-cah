package ch.swissbytes.fqmes.control.user;

import ch.swissbytes.fqmes.model.dao.VerificationTokenDao;
import ch.swissbytes.domain.repository.entities.UserEntity;
import ch.swissbytes.domain.repository.entities.VerificationTokenEntity;
import org.omnifaces.util.Messages;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 17/09/14.
 */
public class VerificationTokenService implements Serializable{

    private static final Logger log = Logger.getLogger(VerificationTokenService.class.getName());

    @Inject
    private VerificationTokenDao tokenDao;


    public VerificationTokenEntity getVerificationTokenGenerated(UserEntity user){
        return tokenDao.generateVerificationToken(user);
    }

    /**
     * Returns the VerificationTokenEntity is has active Token otherwise returns Null
     * @param token
     * @return
     */
    public VerificationTokenEntity getActiveVerificationToken(String token){
        log.info("getActiveVerificationToken(String token["+token+"])");
        List<VerificationTokenEntity> list = tokenDao.getActiveVerifyToken(token);
        VerificationTokenEntity entity = null;
        if(!list.isEmpty()){
            entity = list.get(0);
        }else{
            Messages.addGlobalError("Does not exist verification token!");
        }
        return entity;
    }

    public void  updateToken(VerificationTokenEntity detachedTokenEntity){
        tokenDao.updateToken(detachedTokenEntity);
    }

    private boolean hasExpired(VerificationTokenEntity entity) {
        Date tokenDate = new Date();
        return tokenDate.after(entity.getExpirationDate());
    }

}

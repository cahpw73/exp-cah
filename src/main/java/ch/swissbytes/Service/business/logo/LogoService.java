package ch.swissbytes.Service.business.logo;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.LogoEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class LogoService extends Service<LogoEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(LogoService.class.getName());

    @Inject
    private LogoDao dao;

    @PostConstruct
    public void create(){
        super.initialize(dao);
    }

    public void delete(LogoEntity logo){
        logo.setStatus(StatusEnum.DELETED);
        dao.update(logo);
    }

    public List<LogoEntity> getLogoList(){
        log.info("getCurrencyList");
        return dao.getLogoList();
    }


}

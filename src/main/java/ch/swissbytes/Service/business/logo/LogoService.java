package ch.swissbytes.Service.business.logo;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.LogoEntity;

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

    public LogoService(){
        //super.initialize(dao);
    }

    @PostConstruct
    public void create(){
        super.initialize(dao);
    }

   /* ;

    @Transactional
    public void doSave(LogoEntity logo){
        log.info("doSave");
        dao.doSave(logo);
    }

    public void doUpdate(LogoEntity detachedEntity){
        log.info("doUpdate");
        dao.doUpdate(detachedEntity);
    }*/

    public List<LogoEntity> getLogoList(){
        log.info("getBrandList");
        return dao.getLogoList();
    }


}

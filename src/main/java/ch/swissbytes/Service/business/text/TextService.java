package ch.swissbytes.Service.business.text;


import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.xml.soap.Text;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class TextService implements Serializable {

    private static final Logger log = Logger.getLogger(TextService.class.getName());

    @Inject
    private TextDao dao;

    @Inject
    private TextClausesDao textClausesDao;

    public void doSave(TextEntity entity, POEntity po){
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setPo(po);
        dao.doSave(entity);
        for(ClausesEntity ps: entity.getClausesList()){
            ps.setId(null);
            ps.setText(entity);
            //ps.setClauses(ps.getTextSnippet().getTextSnippet());
            textClausesDao.doSave(ps);
        }
    }

    public void doUpdate(TextEntity entity, POEntity po) {
        if(entity.getId()!=null){
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);

        }else{
            entity.setLastUpdate(new Date());
            entity.setStatus(StatusEnum.ENABLE);
            entity.setPo(po);
            dao.doSave(entity);
        }
        for(ClausesEntity ps: entity.getClausesList()){
            if(ps.getId() >= 1000 || ps.getId() < 0){
                ps.setId(null);
                ps.setText(entity);
            }
            ps.setLastUpdate(new Date());
            textClausesDao.doUpdate(ps);
        }
    }


    public TextEntity findByPoId(Long poEntityId) {
        List<TextEntity> list = dao.findByPoId(poEntityId);
        return !list.isEmpty() ? list.get(0) : new TextEntity();
    }

    @Transactional
    public List<ClausesEntity> findClausesByTextId(Long id) {
        return textClausesDao.findClausesByTextId(id);
    }
}

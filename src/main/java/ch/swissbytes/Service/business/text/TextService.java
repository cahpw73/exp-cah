package ch.swissbytes.Service.business.text;


import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
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
        for(ProjectTextSnippetEntity ps: entity.getClausesList()){
            ClausesEntity clausesEntity = new ClausesEntity();
            clausesEntity.setStatusEnum(StatusEnum.ENABLE);
            clausesEntity.setId(null);
            clausesEntity.setTextSnippet(ps.getTextSnippet());
            clausesEntity.setText(entity);
            clausesEntity.setClauses(ps.getTextSnippet().getTextSnippet());
            textClausesDao.doSave(clausesEntity);
        }
    }

    public void doUpdate(TextEntity entity) {
        entity.setLastUpdate(new Date());
        dao.doUpdate(entity);
        List<ClausesEntity> clausesList = textClausesDao.findByTextId(entity.getId());
        if(!entity.getClausesList().isEmpty()){
            for(ProjectTextSnippetEntity ps: entity.getClausesList()){
                ClausesEntity clausesEntity = new ClausesEntity();
                clausesEntity.setId(null);
                clausesEntity.setTextSnippet(ps.getTextSnippet());
                clausesEntity.setText(entity);
                clausesEntity.setClauses(ps.getTextSnippet().getTextSnippet());
                textClausesDao.doSave(clausesEntity);
            }
            ClausesEntity clauses;
            for(ClausesEntity cl : clausesList){
                clauses = cl;
                if(!entity.getClausesList().contains(cl)){
                    log.info("removing clauses");
                    textClausesDao.doDelete(clauses);
                }
            }
        }else if(!clausesList.isEmpty() && entity.getClausesList().isEmpty()){
            ClausesEntity clauses;
            for(ClausesEntity cl : clausesList){
                clauses = cl;
                if(!entity.getClausesList().contains(cl)){
                    log.info("removing clauses");
                    textClausesDao.doDelete(clauses);
                }
            }
        }
    }


    public TextEntity findByPoId(Long poEntityId) {
        List<TextEntity> list = dao.findByPoId(poEntityId);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<ClausesEntity> findClausesByTextId(Long id) {
        return textClausesDao.findClausesByTextId(id);
    }
}

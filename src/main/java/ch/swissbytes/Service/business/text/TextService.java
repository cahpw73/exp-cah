package ch.swissbytes.Service.business.text;


import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
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

    public void doSave(TextEntity entity, PurchaseOrderProcurementEntity po){
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setPo(po);
        dao.doSave(entity);
        for(ClausesEntity ps: entity.getClausesList()){
            ps.setId(null);
            ps.setText(entity);
            textClausesDao.doSave(ps);
        }
    }

    public void doUpdate(TextEntity entity, PurchaseOrderProcurementEntity po) {
        if(entity.getId()!=null){
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);

        }else{
            entity.setLastUpdate(new Date());
            entity.setStatus(StatusEnum.ENABLE);
            entity.setPo(po);
            dao.doSave(entity);
        }
        Integer order=0;
        for(ClausesEntity ps: entity.getClausesList()){
            if(ps.getId() < 0){
                ps.setId(null);
                ps.setText(entity);
            }
            ps.setOrdered(order);
            order++;
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

    @Transactional
    public List<ClausesEntity> getAll() {
        return textClausesDao.getAll();
    }

    @Transactional
    public void doBulkUpdateCode(List<ProjectTextSnippetEntity> projectTextSnippetList){
        for(ProjectTextSnippetEntity p : projectTextSnippetList){
            List<ClausesEntity> list = textClausesDao.findByProjectTextId(p.getId());
            for(ClausesEntity c : list){
                c.setCode(p.getCode().toUpperCase());
                textClausesDao.doUpdate(c);
            }
        }
    }

    @Transactional
    public void doUpdateClauses(ClausesEntity detachedEntity){
        if(detachedEntity != null){
            ClausesEntity entity = dao.merge(detachedEntity);
            dao.saveAndFlush(entity);
        }
    }
}

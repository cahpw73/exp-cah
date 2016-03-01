package ch.swissbytes.Service.business.poDocument;


import ch.swissbytes.Service.business.mainDocument.AttachmentMainDocumentService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.PODocumentEntity;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderProcurementEntity;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class PODocumentService implements Serializable {

    private static final Logger log = Logger.getLogger(PODocumentService.class.getName());

    @Inject
    private PODocumentDao dao;

    @Inject
    private ProjectDocumentService projectDocumentService;


    public List<PODocumentEntity> findByPOId(final Long poId){
        return dao.findByPOId(poId);
    }

    public void doSave(List<PODocumentEntity> poDocumentList, PurchaseOrderProcurementEntity po){
        int order = 0;
        for(PODocumentEntity ps: poDocumentList){
            if(ps.getId() < 0){
                ps.setId(null);
                ps.setPoProcurementEntity(po);
                ps.setOrdered(order);
                order++;
                ps.setLastUpdate(new Date());
                dao.doSave(ps);
            }else{
                PODocumentEntity entity =  dao.merge(ps);
                entity.setOrdered(order);
                entity.setPoProcurementEntity(po);
                order++;
                entity.setLastUpdate(new Date());
                dao.doSave(entity);
            }
        }
    }

    @Transactional
    public void doSaveNewPODocumentDlg(PODocumentEntity entity){
        dao.doSave(entity);
    }

    @Transactional
    public void doSaveNewPODocumentWithPdf(PODocumentEntity entity){
        dao.doSave(entity);
    }

    public void doUpdate(List<PODocumentEntity> poDocumentList,List<ProjectDocumentEntity> projDocList, PurchaseOrderProcurementEntity po) {
        int order=0;
        for(PODocumentEntity ps: poDocumentList){
            if(ps.getId() < 0){
                ps.setId(null);
            }
            ps.setPoProcurementEntity(po);
            ps.setOrdered(order);
            order++;
            ps.setLastUpdate(new Date());
            dao.doUpdate(ps);
        }
        for(ProjectDocumentEntity pr: projDocList){
            if(pr.getId()<0){
                pr.setId(null);
                projectDocumentService.doSave(pr);
            }
        }
    }

    @Transactional
    public void doBulkUpdateCode(List<ProjectDocumentEntity> projectDocumentList){
        for(ProjectDocumentEntity p : projectDocumentList){
            List<PODocumentEntity> list = dao.findByProjectDocumentId(p.getId());
            for(PODocumentEntity pd : list){
                pd.setCode(p.getCode().toUpperCase());
                dao.doUpdate(pd);
            }
        }
    }
}

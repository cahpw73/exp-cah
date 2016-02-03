package ch.swissbytes.Service.business.poDocument;


import ch.swissbytes.domain.model.entities.PODocumentEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderProcurementEntity;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class PODocumentService implements Serializable {

    private static final Logger log = Logger.getLogger(PODocumentService.class.getName());

    @Inject
    private PODocumentDao dao;

    public List<PODocumentEntity> findByPOId(final Long poId){
        return dao.findByPOId(poId);
    }


}

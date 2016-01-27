package ch.swissbytes.Service.business.poDocument;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.PODocumentEntity;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by christian on 9/10/14.
 */

public class PODocumentDao extends GenericDao<PODocumentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(PODocumentDao.class.getName());


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

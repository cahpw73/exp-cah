package ch.swissbytes.procurement.control.brand;

import ch.swissbytes.domain.repository.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.repository.entities.TransitDeliveryPointEntity;
import ch.swissbytes.domain.repository.types.TimeMeasurementEnum;
import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.control.scopesupply.AttachmentScopeSupplyService;
import ch.swissbytes.fqmes.control.tdp.TransitDeliveryPointService;
import ch.swissbytes.fqmes.model.dao.ScopeSupplyDao;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class BrandService extends Service<ScopeSupplyEntity> implements Serializable{


}

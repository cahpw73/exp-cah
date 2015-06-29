package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportDeliverables extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportDeliverables.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private PurchaseOrderEntity po;
    private Long projectId;
    private String poNo;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportDeliverables(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                              Configuration configuration, PurchaseOrderEntity po, Long projectId, String poNo) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.po = po;
        this.projectId = projectId;
        this.poNo = poNo;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        //addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        //addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        //addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        //addParameters("COUNTRY_LOCALE", configuration.getCountry());
        addParameters("SUBREPORT_DIR","reports/procurement/deliverables/");
        loadParamDeliverables();
    }

    private void loadParamDeliverables() {
        if(po.getProjectEntity().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClientLogo().getFile());
            addParameters("logo", logo);
        }else if(po.getProjectEntity().getDefaultLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getDefaultLogo().getFile());
            addParameters("logo", logo);
        }
        //TODO check this @alvaro
        /*if(po.getProjectEntity().getSupplierProcurement() != null){
            addParameters("company", po.getProjectEntity().getSupplierProcurement().getCompany());
            addParameters("street", po.getProjectEntity().getSupplierProcurement().getStreet());
            addParameters("state", po.getProjectEntity().getSupplierProcurement().getState());
            addParameters("postcode", po.getProjectEntity().getSupplierProcurement().getPostCode());
            addParameters("country", po.getProjectEntity().getSupplierProcurement().getCountry());
            addParameters("phone", po.getProjectEntity().getSupplierProcurement().getPhone());
            addParameters("fax", po.getProjectEntity().getSupplierProcurement().getFax());
        }*/
        addParameters("projectIdFilter", projectId);
        addParameters("poNoFilter", poNo != null ? "%"+poNo+"%" : "");
        addParameters("TIME_ZONE", configuration.getTimeZone());
        Date now = new Date();
        addParameters("currentDate",now);
    }

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    private EntityManager entityManager;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportDeliverables(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                              Configuration configuration, PurchaseOrderEntity po, Long projectId, String poNo,
                              EntityManager entityManager) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.po = po;
        this.projectId = projectId;
        this.poNo = poNo;
        this.entityManager = entityManager;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("SUBREPORT_DIR","reports/procurement/deliverables/");
        addParameters("FORMAT_DATE_TIME", configuration.getFormatDateTime());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        loadParamDeliverables();
    }

    private void loadParamDeliverables() {
        addParameters("projectCode", po.getProjectEntity().getProjectNumber());
        addParameters("projectName", po.getProjectEntity().getTitle());
        addParameters("projectCurrency",getCurrencyDefault());
        addParameters("client", po.getProjectEntity().getClient().getName());
        if(po.getProjectEntity().getClient()!=null && po.getProjectEntity().getClient().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientLogo().getFile());
            addParameters("logoFooter", logo);
        }
        addParameters("projectIdFilter", projectId);
        addParameters("poNoFilter", poNo != null ? "%"+poNo+"%" : "");
        Date now = new Date();
        addParameters("currentDate",Util.convertUTC(now,configuration.getTimeZone()));
    }

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getCurrencyDefault() {
        String currencyDefault = "";
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectCurrencyEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.project.id = :PROJECT_ID ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("ENABLE",StatusEnum.ENABLE);
        query.setParameter("PROJECT_ID",projectId);
        List<ProjectCurrencyEntity> list = query.getResultList();
        log.info("List ProjectCurrencyEntity : " + list.size());
        for(ProjectCurrencyEntity pc : list){
            if(pc.getProjectDefault()){
                currencyDefault = pc.getCurrency().getName();
            }
        }

        return currencyDefault;
    }
}

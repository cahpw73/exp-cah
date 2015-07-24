package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;
import ch.swissbytes.procurement.report.dtos.ProjectProcurementDto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportProjectProcurement extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportProjectProcurement.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private List<PurchaseOrderEntity> poList;
    private ProjectEntity project;
    private String strSortBy;



    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportProjectProcurement(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                    Configuration configuration, List<PurchaseOrderEntity> poList, ProjectEntity project, String strSortBy) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.poList = poList;
        this.project = project;
        this.strSortBy = strSortBy;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        addParameters("FORMAT_DATE_TIME", configuration.getFormatDateTime());
        addParameters("STATUS_PROCUREMENT",lookupValueFactory.getStatusPOProcurement());
        addParameters("SUBREPORT_DIR", "reports/procurement/uncommittedDataReport/");
        loadParamDeliverables();
    }

    private void loadParamDeliverables() {
        addParameters("PROJECT_ID", project.getId());
        addParameters("projectCode", project.getProjectNumber());
        addParameters("projectName", project.getTitle());
        addParameters("projectCurrency",getCurrencyDefault());
        addParameters("client", project.getClient().getName());
        addParameters("sortBy", strSortBy);
        Date now = new Date();
        addParameters("currentDate",Util.convertUTC(now,configuration.getTimeZone()));

        if(project.getClient()!=null && project.getClient().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(project.getClient().getClientLogo().getFile());
            addParameters("logoFooter", logo);
        }else if(project.getClient()!=null && project.getClient().getDefaultLogo()!=null){
            InputStream logo = new ByteArrayInputStream(project.getClient().getDefaultLogo().getFile());
            addParameters("logoFooter", logo);
        }
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
        for(ProjectCurrencyEntity pc : project.getCurrencies()){
            if(pc.getProjectDefault()){
                currencyDefault = pc.getCurrency().getName();
            }
        }
        return currencyDefault;
    }
}

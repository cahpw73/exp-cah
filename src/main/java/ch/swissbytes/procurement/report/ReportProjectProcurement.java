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
        //addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        //addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        //addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        //addParameters("COUNTRY_LOCALE", configuration.getCountry());
        loadParamDeliverables();
    }

    private void loadParamDeliverables() {
        List<ProjectProcurementDto> dtos = getProjectProcurementDtos();
        //InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getReportLogo().getFile());
        //addParameters("logo", logo);
        //addParameters("client", "");
        addParameters("projectCode", project.getProjectNumber());
        addParameters("projectName", project.getTitle());
        addParameters("projectCurrency",getCurrencyDefault());
        addParameters("sortBy", strSortBy);
        addParameters("pooList",createDataSource(dtos));
        Date now = new Date();
        addParameters("currentDate",now);
    }

    private List<ProjectProcurementDto> getProjectProcurementDtos() {
        List<ProjectProcurementDto> dtos = new ArrayList<>();
        for(Object element : poList){
            Object []values = (Object []) element;
            ProjectProcurementDto dto = new ProjectProcurementDto();
            dto.setPo(((String)values[0]));
            dto.setVariation(((String)values[1]));
            dto.setOrderDate(((Date)values[2]));
            dto.setCompany(((String)values[3]));
            dto.setOrderTitle(((String)values[4]));
            dto.setCurrency(((String)values[5]));
            dto.setPoDeliveryDate(((Date)values[6]));
            dto.setPoStatus(((POStatusEnum) values[7]).getLabel());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport(null);
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

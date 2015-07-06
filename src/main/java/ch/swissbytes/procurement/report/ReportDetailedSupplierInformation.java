package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportDetailedSupplierInformation extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportDetailedSupplierInformation.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private ProjectEntity project;
    Map<String, Boolean> sortMap;



    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link Locale}
     */
    public ReportDetailedSupplierInformation(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                             Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.project = project;
        this.sortMap = sortMap;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        //addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        //addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        //addParameters("COUNTRY_LOCALE", configuration.getCountry());
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        addParameters("SUBREPORT_DIR","reports/procurement/DetailedSupplierInformation/");
        loadParamDeliverables();
    }

    private void loadParamDeliverables() {

        //InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getReportLogo().getFile());
        //addParameters("logo", logo);
        addParameters("client", project.getClient()!=null?project.getClient().getTitle():"");
        addParameters("PROJECT_ID", project.getId());
        addParameters("projectCode", project.getProjectNumber());
        addParameters("projectName", project.getTitle());
        addParameters("projectCurrency",getCurrencyDefault());
        addParameters("sortBy", getStrSort());
        addParameters("projectIdFilter",project.getId());
        Date now = new Date();
        addParameters("currentDate", now);
    }

    private String getStrSort(){
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        String strSort = "";
        if(poNo){
            strSort = strSort+"po,";
        }
        if (supplier){
            strSort = strSort+"company,";
        }

        if(strSort.length()>1){
            strSort = strSort.substring(0,strSort.length() - 1);
        }
        if(strSort.isEmpty()){
            strSort ="id";
        }
        return strSort;
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

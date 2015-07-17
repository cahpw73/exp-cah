package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportDetailedSupplierInformation extends ReportProject {

  /*  private final Logger log = Logger.getLogger(ReportDetailedSupplierInformation.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private ProjectEntity project;
    Map<String, Boolean> sortMap;
    private String sortByName="";*/



    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link Locale}
     */
    public ReportDetailedSupplierInformation(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                             Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);
      /*  this.configuration = configuration;
        this.project = project;
        this.sortMap = sortMap;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        //addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        //addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        //addParameters("COUNTRY_LOCALE", configuration.getCountry());
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());

        loadParamDeliverables();*/
    }

    protected void loadAdditionalParameters() {
      /*  if(project.getClient()!=null && project.getClient().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(project.getClient().getClientLogo().getFile());
            addParameters("logoFooter", logo);
        }else if(project.getClient()!=null && project.getClient().getDefaultLogo()!=null){
            InputStream logo = new ByteArrayInputStream(project.getClient().getDefaultLogo().getFile());
            addParameters("logoFooter", logo);
        }

        addParameters("client", project.getClient()!=null?project.getClient().getTitle():"");*/
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR","reports/procurement/DetailedSupplierInformation/");
       /* addParameters("projectCode", project.getProjectNumber());
        addParameters("projectName", project.getTitle());
        addParameters("projectCurrency",getCurrencyDefault());
        addParameters("sortBy", getStrSort());
        addParameters("projectIdFilter",project.getId());
        Date now = new Date();
        addParameters("currentDate", now);*/
    }

   /* private String getStrSort(){
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        Boolean varNo = sortMap.get("varNo");
        String strSort = "";
        if(poNo){
            strSort = strSort+"po.po, ";
            sortByName = sortByName + "Po No, ";
        }
        if(varNo){
            strSort = strSort+"po.orderedvariation, ";
            sortByName = sortByName +  "Var No,";
        }
        if (supplier){
            strSort = strSort+"sp.company, ";
            sortByName = sortByName +  "Supplier, ";
        }

        if(strSort.length()>1){
            sortByName = sortByName.substring(0,sortByName.length()-2);
            strSort = strSort.substring(0,strSort.length() - 2);
        }
        System.out.println("sortBy create: " + strSort);
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
    }*/
}

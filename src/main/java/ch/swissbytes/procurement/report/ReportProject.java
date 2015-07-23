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
public class ReportProject extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportProject.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    protected ProjectEntity project;
    Map<String, Boolean> sortMap;
    protected String sortByName = "";




    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link Locale}
     */
    public ReportProject(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                         Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.project = project;
        this.sortMap = sortMap;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
       // addParameters("SUBREPORT_DIR","reports/procurement/detailedProcurementReport/");
        addParameters("STATUS_PROCUREMENT",lookupValueFactory.getStatusPOProcurement());
        loadParameters();
        loadAdditionalParameters();
    }
    protected void loadAdditionalParameters(){
        // i
    }

    private void loadParameters() {
        addParameters("projectIdFilter",project.getId());
        addParameters("projectCode", project.getProjectNumber());
        addParameters("projectName", project.getTitle());
        addParameters("projectCurrency",getCurrencyDefault());
        addParameters("client", project.getClient().getName());
        addParameters("sortBy", getStrSort());
        addParameters("sortByName",sortByName);
        Date now = new Date();
        addParameters("currentDate",now);

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

    protected String getStrSort(){
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
        return strSort;
    }
}

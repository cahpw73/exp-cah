package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;

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
        addParameters("FORMAT_DATE_TIME", configuration.getFormatDateTime());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        addParameters("STATUS_PROCUREMENT",lookupValueFactory.getStatusPOProcurement());
        loadParameters();
        loadAdditionalParameters();
    }
    protected void loadAdditionalParameters(){
    }

    private void loadParameters() {
        if(project.getClient()!=null) {
            addParameters("projectIdFilter", project.getId());
            addParameters("projectCode", project.getProjectNumber());
            addParameters("projectName", project.getTitle());
            ProjectCurrencyEntity defaultCurrency=getCurrencyDefault();
            addParameters("projectCurrency", defaultCurrency!=null?defaultCurrency.getCurrency().getCode():null);
            addParameters("projectDefaultExchangeRate",defaultCurrency!=null?defaultCurrency.getExchangeRate():null);
            addParameters("client", project.getClient() != null ? project.getClient().getName() : "");
            if(project.getClient()!=null && project.getClient().getClientLogo()!=null){
                InputStream logo = new ByteArrayInputStream(project.getClient().getClientLogo().getFile());
                addParameters("logoFooter", logo);
            }
        }
        addParameters("sortBy", getStrSort());
        addParameters("sortByName",sortByName);
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

    public ProjectCurrencyEntity getCurrencyDefault() {
        ProjectCurrencyEntity currencyDefault = null;

        for(ProjectCurrencyEntity pc : project.getCurrencies()){
            if(pc.getProjectDefault()){
                currencyDefault = pc;
                break;
            }
        }
        if(project.getCurrencies().size()>=1&&currencyDefault==null){
            return project.getCurrencies().get(0);
        }
        return currencyDefault;
    }
   /* public BigDecimal getExchangeRateDefault() {
        BigDecimal exchangeRate = new BigDecimal("0");
        for(ProjectCurrencyEntity pc : project.getCurrencies()){
            if(pc.getProjectDefault()){
                exchangeRate = pc.getExchangeRate();
                break;
            }
        }
        return exchangeRate;
    }*/

    protected String getStrSort(){
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        Boolean deliveryDate = sortMap.get("deliveryDate");
        String strSort = "";
        if(poNo && supplier && deliveryDate){
            strSort = "po.po,po.orderedvariation,sp.company,po.po_delivery_date, ";
            sortByName =  "Po No, Variation, Supplier, Delivery Date, ";
        }else if(!poNo && supplier && deliveryDate){
            strSort = "sp.company,po.po_delivery_date,po.orderedvariation, ";
            sortByName =  "Supplier, Delivery Date, Variation, ";
        }else if(!poNo && !supplier && deliveryDate){
            strSort = "po.po_delivery_date,po.orderedvariation, ";
            sortByName =  "Delivery Date, Variation, ";
        }else if(poNo && supplier && !deliveryDate){
            strSort = "po.po,po.orderedvariation,sp.company, ";
            sortByName =  "Po No, Variation, Supplier, ";
        }else if(poNo && !supplier && !deliveryDate){
            strSort = "po.po,po.orderedvariation, ";
            sortByName =  "Po No, Variation, ";
        }else if(!poNo && supplier && !deliveryDate){
            strSort = "sp.company,po.orderedvariation, ";
            sortByName =  "Supplier, Variation, ";
        }else if(poNo && !supplier && deliveryDate){
            strSort = "po.po,po.orderedvariation,po.po_delivery_date, ";
            sortByName =  "Po No, Variation, Delivery Date, ";
        }

        if(strSort.length()>1){
            sortByName = sortByName.substring(0,sortByName.length()-2);
            strSort = strSort.substring(0,strSort.length() - 2);
        }else{
            sortByName = "Variation";
            strSort = "po.orderedvariation";
        }
        return strSort;
    }
}

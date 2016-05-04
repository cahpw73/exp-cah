package ch.swissbytes.fqmes.report;




import ch.swissbytes.fqmes.report.util.DocTypeEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportNextKey extends ReportView implements Serializable {


    private EntityManager entityManager;

    private final Logger log = Logger.getLogger(ReportNextKey.class.getName());

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

  //  @Inject
    private Configuration configuration;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportNextKey(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                         EntityManager entityManager, final List<Long> ids, Configuration configuration, DocTypeEnum docTypeEnum, DataSource ds) {
        super(filenameJasper, reportNameMsgKey, messages, locale,ds);
        setReportType(docTypeEnum);
        this.configuration = configuration;
        this.entityManager=entityManager;
        if(ids!=null&&ids.size()>0){
            log.info("List Ids PurchaseOrder: " + ids.size());
            addParameters("purchaseID", ids);
            for(Long id : ids){
                log.info("Id : "+ id);
            }
        }
        addParameters("SUBREPORT_DIR","reports/nextKeyInfo/");
        LookupValueFactory lookupValueFactory=new LookupValueFactory();
        addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        addParameters("COUNTRY_LOCALE", configuration.getCountry());
        addParameters("STATUS_EXPEDITING",lookupValueFactory.getPurchaseOrderStatuses());
        loadParamJobSummary();
    }

    private void loadParamJobSummary() {
        addParameters("itemLbl", "Item No");
        addParameters("qtyLbl", "Qty");
        addParameters("uomLbl", "Unit");
        addParameters("titleLbl", "Item Description");
        addParameters("equipmentLbl", "Equipment Tag");
        addParameters("incoTermLbl", "INCO term");
        addParameters("deliveryDateLbl", "Delivery Date");
        addParameters("forecastExWorksDateLbl", "Forecast Ex Works Date");
        addParameters("actualExWorksDateLbl", "Actual Ex Works Date");
        addParameters("leadTimeLbl", "Lead Time");
        addParameters("forecastSiteDateLbl", "Forecast Site Date");
        addParameters("actualSiteDateLbl", "Actual Site Date");
        addParameters("requiredOnSiteDateLbl", "Required on Site Date");
        addParameters("deliveryCommentLbl", "Delivery Comment");
        addParameters("fullIncoTermLbl", "Full Inco Term");
        addParameters("varLbl", "Var");
        addParameters("TIME_ZONE",configuration.getTimeZone());
        Date now=new Date();
        now.setHours(23);
        now.setMinutes(59);
        now.setSeconds(59);
        addParameters("CURRENT_DATE", Util.convertUTC(now, TimeZone.getDefault().getID()));
    }

    @Override
    public void printDocument(Long documentId) {
        try{
            runReport();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }



}

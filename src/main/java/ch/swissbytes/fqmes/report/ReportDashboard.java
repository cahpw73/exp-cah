package ch.swissbytes.fqmes.report;




import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.ResourceUtils;

import javax.persistence.EntityManager;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportDashboard extends ReportView implements Serializable {


    private EntityManager entityManager;
    private ResourceUtils resourceUtils;

    private final Logger log = Logger.getLogger(ReportDashboard.class.getName());

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

  //  @Inject
    private Configuration configuration;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportDashboard(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale, EntityManager entityManager, Configuration configuration,Map<String,String> parameterDashboard) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.entityManager=entityManager;
        addParameters("SUBREPORT_DIR","reports/jobSummary/");
        LookupValueFactory lookupValueFactory=new LookupValueFactory();
        addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        addParameters("COUNTRY_LOCALE", configuration.getCountry());
        addParameters("STATUS_EXPEDITING",lookupValueFactory.getPurchaseOrderStatuses());
        loadParamJobSummary(parameterDashboard);
    }

    private void loadParamJobSummary(Map<String,String> parameterDashboard) {
        resourceUtils = new ResourceUtils();
        addParameters("totalNumberPOs", parameterDashboard.get("totalOfPOs"));
        addParameters("completedPOs", parameterDashboard.get("completedPOs"));
        addParameters("openPOs", parameterDashboard.get("openPOs"));
        addParameters("deliveryNextMoth", parameterDashboard.get("deliveryNextMoth"));
        addParameters("deliveryNext3Moth", parameterDashboard.get("deliveryNext3Moth"));
        addParameters("mrrOutStanding", parameterDashboard.get("mrrsOutstanding"));
        addParameters("dashboardTitle",parameterDashboard.get("dashboardTitle"));
        InputStream iconTotalPO = resourceUtils.getResourceAsStream("/images/total-pos.png");
        addParameters("iconTotalPO", iconTotalPO);
        InputStream iconCompletedPO = resourceUtils.getResourceAsStream("/images/completed-pos.png");
        addParameters("iconCompletedPO", iconCompletedPO);
        InputStream iconOpenPO = resourceUtils.getResourceAsStream("/images/open-pos.png");
        addParameters("iconOpenPO", iconOpenPO);
        InputStream iconDeliveryNextMothPO = resourceUtils.getResourceAsStream("/images/delivery-next-moth.png");
        addParameters("iconDeliveryNextMothPO", iconDeliveryNextMothPO);
        InputStream iconDeliveryNext3MothPO = resourceUtils.getResourceAsStream("/images/delivery-next-3-moth.png");
        addParameters("iconDeliveryNext3MothPO", iconDeliveryNext3MothPO);
        InputStream iconMrrOutstanding = resourceUtils.getResourceAsStream("/images/mrr-outstanding.png");
        addParameters("iconMrrOutstanding", iconMrrOutstanding);
    }

    @Override
    public void printDocument(Long documentId) {
        try{
            runReport(null);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }



}

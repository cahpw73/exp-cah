package ch.swissbytes.fqmes.report;



import ch.swissbytes.domain.repository.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportPurchaseOrder extends ReportView implements Serializable {


    private EntityManager entityManager;

    private final Logger log = Logger.getLogger(ReportPurchaseOrder.class.getName());

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

  //  @Inject
    private Configuration configuration;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale, EntityManager entityManager, final List<PurchaseOrderEntity> orders,Configuration configuration) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.entityManager=entityManager;
         Collection<Long>ids=new ArrayList<>();
        for(PurchaseOrderEntity entity:orders ){
            ids.add(entity.getId());
        }
        if(ids.size()>0){
            addParameters("purchaseID",ids);
        }
        addParameters("SUBREPORT_DIR","reports/jobSummary/");
        LookupValueFactory lookupValueFactory=new LookupValueFactory();
        addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        addParameters("COUNTRY_LOCALE", configuration.getCountry());
        loadParamJobSummary();
    }

    private void loadParamJobSummary() {
        addParameters("itemLbl", "Item No");
        addParameters("qtyLbl", "Qty");
        addParameters("uomLbl", "Unit");
        addParameters("titleLbl", "Title");
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
        System.out.println("UTC CURRENT DATE "+Util.convertUTC(now, TimeZone.getDefault().getID()));
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

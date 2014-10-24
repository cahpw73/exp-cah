package ch.swissbytes.fqmes.report;



import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;

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


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale, EntityManager entityManager, final List<PurchaseOrderEntity> orders) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        Configuration configuration = new Configuration();
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

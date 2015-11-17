package ch.swissbytes.procurement.jobs;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by christian on 09-01-15.
 */
@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class JobExportCMSandJDE implements Serializable {

    private final Logger log = Logger.getLogger(JobExportCMSandJDE.class.getName());

    @EJB
    private ExportationPOBean exportationPOBean;

    @PostConstruct
    public void initialize() {
        log.info("Service Started JobExportCMSandJDE");
    }
    @PreDestroy
    public void terminate() {
        log.info("Shut down in progress JobExportCMSandJDE");
    }

    @Schedule(dayOfMonth = "*", hour = "14", minute = "45", info = "Every day at 1:00 am", persistent = false)
    public void startExportationDaily() {
        log.info("startExportationDaily");
        try {
            exportationPOBean.dailyExportation();
        } catch (Exception e) {
            log.info("Error background job CronNotifier.startTracingActivities " + e.getMessage());
        }
    }

    /*@Schedule( dayOfMonth = "*", hour = "2", minute = "0", info = "Every day at 2:00 am", persistent = false)
    public void startArchiveRequest() {
        log.info("startArchiveRequest");
        try {
            log.info("start archive requests ");
            //verificationRequestAction.doArchiveRequest();
        } catch (Exception e) {
            //log.error("Error background job CronNotifier.startTracingActivities " + e.getMessage());
        }
    }*/
}

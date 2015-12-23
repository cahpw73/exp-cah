package ch.swissbytes.procurement.jobs;

import ch.swissbytes.fqmes.util.CreateEmailSender;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by christian on 09-01-15.
 */
@Startup
@Singleton
public class JobExportCMSandJDE implements Serializable {

    private final Logger log = Logger.getLogger(JobExportCMSandJDE.class.getName());

    @EJB
    private ExportationPOBean exportationPOBean;

    @Inject
    private CreateEmailSender createEmailSender;

    @PostConstruct
    public void initialize() {
        log.info("Service Started JobExportCMSandJDE");
    }
    @PreDestroy
    public void terminate() {
        log.info("Shut down in progress JobExportCMSandJDE");
    }

    @Schedule(dayOfMonth = "*", hour = "3", minute = "30", info = "Every day at 00:00 am")
    public void startExportationDaily() {
        log.info("startExportationDaily");
        try {
            exportationPOBean.dailyExportation(createEmailSender);
        } catch (Exception e) {
            log.info("Error background job CronNotifier.startTracingActivities " + e.getMessage());
        }
    }
}

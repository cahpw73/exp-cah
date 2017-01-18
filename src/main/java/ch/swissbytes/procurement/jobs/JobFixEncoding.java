package ch.swissbytes.procurement.jobs;

import ch.swissbytes.fqmes.util.CreateEmailSender;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by christian on 09-01-15.
 */
@Startup
@Singleton
public class JobFixEncoding implements Serializable {

    private final Logger log = Logger.getLogger(JobFixEncoding.class.getName());

    @EJB
    private FixEncodingBean fixEncodingBean;


    @PostConstruct
    public void initialize() {
        log.info("Service Started JobExportCMSandJDE");
    }
    @PreDestroy
    public void terminate() {
        log.info("Shut down in progress JobExportCMSandJDE");
    }

    @Schedule(dayOfMonth = "*", hour = "*", minute = "53", info = "Every day at 00:00 am")
    public void startFixEncode() {
        log.info("startFixEncode");
        try {
            fixEncodingBean.fixTextEncode();
        } catch (Exception e) {
            log.info("Error background job CronNotifier.startTracingActivities " + e.getMessage());
        }
    }
}

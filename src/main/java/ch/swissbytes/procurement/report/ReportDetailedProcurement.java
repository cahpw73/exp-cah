package ch.swissbytes.procurement.report;



import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;
import java.util.*;

/**
 * Created by christian on 11/06/14.
 */
public class ReportDetailedProcurement extends ReportProject {

    public ReportDetailedProcurement(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                     Configuration configuration, ProjectEntity project,  final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);
    }

    protected void loadAdditionalParameters() {
        addParameters("SUBREPORT_DIR", "reports/procurement/detailedProcurementReport/");
    }
}

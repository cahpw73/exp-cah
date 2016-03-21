package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportUncommittedData extends ReportProject {


    public ReportUncommittedData(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                 Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap,DataSource ds) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap,ds);
    }

    protected void loadAdditionalParameters() {
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR", "reports/procurement/uncommittedDataReport/");
    }
}

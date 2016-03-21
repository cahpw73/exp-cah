package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportRequiredSecurityDeposits extends ReportProject implements Serializable {

    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportRequiredSecurityDeposits(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                          Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap,DataSource ds) {
        super(filenameJasper, reportNameMsgKey, messages, locale, configuration, project, sortMap,ds);
        loadAdditionalParameters();
    }

    protected void loadAdditionalParameters() {
        addParameters("SUBREPORT_DIR", "reports/procurement/RequiredSecurityReport/");
    }

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

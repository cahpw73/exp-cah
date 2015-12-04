package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportSummaryPurchaseOrder extends ReportProject implements Serializable {

    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportSummaryPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                      Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);
    }

    @Override
    protected void loadAdditionalParameters() {
        addParameters("SUBREPORT_DIR","reports/procurement/summaryPurchaseOrderReport/");
        addParameters("sortBySummary", getSortMainSummary());
    }

    protected String getSortMainSummary() {
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        Boolean deliveryDate = sortMap.get("deliveryDate");
        String strSort = "";
        if (poNo && !supplier && !deliveryDate) {
            strSort = "po.po, ";
            sortByName = "PO No, ";
        } else if (!poNo && supplier && !deliveryDate) {
            strSort = "sp.company, ";
            sortByName = "Supplier, ";
        }

        if (strSort.length() > 1) {
            sortByName = sortByName.substring(0, sortByName.length() - 2);
            strSort = strSort.substring(0, strSort.length() - 2);
        } else {
            sortByName = "Variation";
            strSort = "po.po";
        }
        return strSort;
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

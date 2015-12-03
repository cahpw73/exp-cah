package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportRequiredRetentions extends ReportProject implements Serializable {

    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportRequiredRetentions(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                    Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale, configuration, project, sortMap);
        loadAdditionalParameters();
    }

    protected void loadAdditionalParameters() {
        addParameters("SUBREPORT_DIR", "reports/procurement/RequiredRetentionReport/");
        addParameters("sortByPo", getStrSortByPO());
        addParameters("sortBySupplier", getStrSortBySupplier());
    }

    private String getStrSortByPO() {
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        String strSort = "";
        if (poNo) {
            strSort = "po.po,po.orderedvariation, ";
        }

        if (strSort.length() > 1) {
            strSort = strSort.substring(0, strSort.length() - 2);
        }else {
            if (!supplier && !poNo) {
                strSort = "po.orderedvariation";
            }
        }
        return strSort;
    }

    private String getStrSortBySupplier() {
        Boolean supplier = sortMap.get("supplier");
        Boolean poNo = sortMap.get("poNo");
        String strSort = "";
        if (supplier) {
            strSort = "sp.company,po.orderedvariation, ";
        }
        if (strSort.length() > 1) {
            strSort = strSort.substring(0, strSort.length() - 2);
        } else {
            if (!supplier && !poNo) {
                strSort = "po.orderedvariation";
            }
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

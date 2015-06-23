package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportProjectProcurement extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportProjectProcurement.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private List<DeliverableDto> dtos;
    private PurchaseOrderEntity po;
    private Long projectId;
    private String poNo;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportProjectProcurement(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                    Configuration configuration, List<DeliverableDto> dtos, PurchaseOrderEntity po, Long projectId, String poNo) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.dtos = dtos;
        this.po = po;
        this.projectId = projectId;
        this.poNo = poNo;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        //addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        //addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        //addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        //addParameters("COUNTRY_LOCALE", configuration.getCountry());
        loadParamDeliverables();
    }

    private void loadParamDeliverables() {
        InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getReportLogo().getFile());
        addParameters("logo", logo);
        addParameters("client", po.getProjectEntity().getSupplierProcurement().getCompany());
        addParameters("projectCode", po.getProjectEntity().getSupplierProcurement().getStreet());
        addParameters("projectName", po.getProjectEntity().getSupplierProcurement().getState());
        addParameters("projectCurrency", po.getProjectEntity().getSupplierProcurement().getPostCode());
        addParameters("sortedBy", po.getProjectEntity().getSupplierProcurement().getCountry());
        addParameters("currentDate", po.getProjectEntity().getSupplierProcurement().getPhone());
        addParameters("sortBy", po.getProjectEntity().getSupplierProcurement().getFax());
        addParameters("sortBy", po.getProjectEntity().getSupplierProcurement().getFax());
        Date now = new Date();
        now.setHours(23);
        now.setMinutes(59);
        now.setSeconds(59);
        addParameters("CURRENT_DATE", Util.convertUTC(now, TimeZone.getDefault().getID()));
    }

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

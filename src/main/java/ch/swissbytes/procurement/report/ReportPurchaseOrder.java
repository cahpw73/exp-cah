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
public class ReportPurchaseOrder extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportPurchaseOrder.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private PurchaseOrderEntity po;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                               Configuration configuration,PurchaseOrderEntity po) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.po = po;
        LookupValueFactory lookupValueFactory = new LookupValueFactory();
        //addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());
        //addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        //addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        //addParameters("COUNTRY_LOCALE", configuration.getCountry());
        loadParamPurchaseOrder();
    }

    private void loadParamPurchaseOrder() {
        InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getReportLogo().getFile());
        addParameters("purchaseOrderId",po.getId());
        addParameters("logo", logo);
        addParameters("company", po.getProjectEntity().getSupplierProcurement().getCompany());
        addParameters("street", po.getProjectEntity().getSupplierProcurement().getStreet());
        addParameters("state", po.getProjectEntity().getSupplierProcurement().getState());
        addParameters("postcode", po.getProjectEntity().getSupplierProcurement().getPostCode());
        addParameters("country", po.getProjectEntity().getSupplierProcurement().getCountry());
        addParameters("phone", po.getProjectEntity().getSupplierProcurement().getPhone());
        addParameters("fax", po.getProjectEntity().getSupplierProcurement().getFax());
        addParameters("poNo",po.getPoEntity().getOrderNumber());
        addParameters("orderDate",po.getPoEntity().getOrderDate());
        addParameters("deliveryDate",po.getPoDeliveryDate());
        addParameters("deliverablePoint",po.getPoEntity().getPoint());
        addParameters("deliveryInstructions",po.getPoEntity().getDeliveryInstruction());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        Date now = new Date();
        now.setHours(23);
        now.setMinutes(59);
        now.setSeconds(59);
        addParameters("CURRENT_DATE", Util.convertUTC(now, TimeZone.getDefault().getID()));
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

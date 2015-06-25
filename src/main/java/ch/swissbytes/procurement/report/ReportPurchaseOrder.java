package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.util.ResourceUtils;

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
    private ResourceUtils resourceUtils;
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
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        //addParameters("LANGUAGE_LOCALE", configuration.getLanguage());
        //addParameters("COUNTRY_LOCALE", configuration.getCountry());
        loadParamPurchaseOrder();
    }

    private void loadParamPurchaseOrder() {
        resourceUtils = new ResourceUtils();
        //TODO Maybe this changes after we receive the client details
        if(po.getProjectEntity().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClientLogo().getFile());
            addParameters("logo", logo);
        }else if(po.getProjectEntity().getDefaultLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getDefaultLogo().getFile());
            addParameters("logo", logo);
        }

        if(po.getProjectEntity().getClientFooter()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClientFooter().getFile());
            addParameters("footerLogo", logo);
        }else if(po.getProjectEntity().getDefaultFooter()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getDefaultFooter().getFile());
            addParameters("footerLogo", logo);
        }
        addParameters("purchaseOrderId",po.getId());
        if(po.getProjectEntity().getSupplierProcurement() != null){
            addParameters("company", po.getProjectEntity().getSupplierProcurement().getCompany());
            addParameters("street", po.getProjectEntity().getSupplierProcurement().getStreet());
            addParameters("state", po.getProjectEntity().getSupplierProcurement().getState());
            addParameters("postcode", po.getProjectEntity().getSupplierProcurement().getPostCode());
            addParameters("country", po.getProjectEntity().getSupplierProcurement().getCountry());
            addParameters("phone", po.getProjectEntity().getSupplierProcurement().getPhone());
            addParameters("fax", po.getProjectEntity().getSupplierProcurement().getFax());
        }
        addParameters("poNo",po.getPo());
        addParameters("orderDate",po.getPoEntity().getOrderDate());
        addParameters("deliveryDate",po.getPoDeliveryDate());
        addParameters("deliveryPoint",po.getPoEntity().getPoint());
        addParameters("deliveryInstructions",po.getPoEntity().getDeliveryInstruction());
        addParameters("procManager",po.getPoEntity().getProcManager());
        addParameters("procManagerDetail",po.getPoEntity().getProcManagerDetail());
        if(po.getPoEntity().getPoProcStatus().ordinal() != POStatusEnum.FINAL.ordinal()){
            InputStream watermark = resourceUtils.getResourceAsStream("/images/draft-report.jpg");
            addParameters("watermarkDraft",watermark);
        }
        Date now = new Date();
        addParameters("currentDate",now);
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

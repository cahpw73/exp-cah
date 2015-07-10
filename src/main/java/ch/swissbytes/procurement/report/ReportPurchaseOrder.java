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
        if(po.getProjectEntity().getClient().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientLogo().getFile());
            addParameters("logo", logo);
        }else if(po.getProjectEntity().getClient().getDefaultLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getDefaultLogo().getFile());
            addParameters("logo", logo);
        }

        if(po.getProjectEntity().getClient().getClientFooter()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientFooter().getFile());
            addParameters("footerLogo", logo);
        }else if(po.getProjectEntity().getClient().getDefaultFooter()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getDefaultFooter().getFile());
            addParameters("footerLogo", logo);
        }
        addParameters("purchaseOrderId",po.getId());

        if(po.getPoEntity().getSupplier() != null){
            addParameters("company", po.getPoEntity().getSupplier().getCompany());
            addParameters("street", po.getPoEntity().getSupplier().getStreet());
            addParameters("state", po.getPoEntity().getSupplier().getState());
            addParameters("postcode", po.getPoEntity().getSupplier().getPostCode());
            addParameters("country", po.getPoEntity().getSupplier().getCountry());
            addParameters("phone", po.getPoEntity().getSupplier().getPhone());
            addParameters("fax", po.getPoEntity().getSupplier().getFax());
        }
        if(po.getProjectEntity().getClient()!=null) {
            addParameters("clientName", po.getProjectEntity().getClient().getName().trim());
            addParameters("clientDetail",new ch.swissbytes.Service.processor.Processor().processSnippetText(po.getProjectEntity().getClient().getTitle()));
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

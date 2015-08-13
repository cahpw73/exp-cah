package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.processor.Processor;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.ClausesEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.report.dtos.PurchaseOrderReportDto;
import ch.swissbytes.procurement.util.ResourceUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
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
    private List<ScopeSupplyEntity> scopeSupplyList;
    private String preamble;
    private List<ClausesEntity> clausesList;
    private Connection connection;


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                               Configuration configuration,PurchaseOrderEntity po, List<ScopeSupplyEntity> scopeSupplyList, String preamble,List<ClausesEntity> clausesList,CashflowEntity cashflowEntity) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.po = po;
        this.scopeSupplyList = scopeSupplyList;
        this.preamble = preamble;
        this.clausesList = clausesList;
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("FORMAT_DATE2", configuration.getHardFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        addParameters("SUBREPORT_DIR", "reports/procurement/printPo/");
        try {
            connection = getConnection();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        addParameters("REPORT_CONNECTION", connection);
        loadParamPurchaseOrder();
        System.out.println("cashflow "+cashflowEntity);
        //System.out.println("Label PAYMENt TERM  "+cashflowEntity.getPaymentTerms()!=null?cashflowEntity.getPaymentTerms().getLabel():"nothing");
        addParameters("paymentTerm", cashflowEntity != null &&cashflowEntity.getPaymentTerms()!=null? cashflowEntity.getPaymentTerms().getLabel() : null);
    }

    private void loadParamPurchaseOrder() {
        resourceUtils = new ResourceUtils();
        if(po.getProjectEntity().getClient()!=null && po.getProjectEntity().getClient().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientLogo().getFile());
            addParameters("logo", logo);
        }else if(po.getProjectEntity().getClient()!=null && po.getProjectEntity().getClient().getDefaultLogo()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getDefaultLogo().getFile());
            addParameters("logo", logo);
        }

        if(po.getProjectEntity().getClient()!=null && po.getProjectEntity().getClient().getClientFooter()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientFooter().getFile());
            addParameters("footerLogo", logo);
        }else if(po.getProjectEntity().getClient()!=null && po.getProjectEntity().getClient().getDefaultFooter()!=null){
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getDefaultFooter().getFile());
            addParameters("footerLogo", logo);
        }
        addParameters("purchaseOrderId", po.getId());

        if(po.getPoEntity().getSupplier() != null){
            addParameters("company", po.getPoEntity().getSupplier().getCompany());
            addParameters("street", po.getPoEntity().getSupplier().getStreet());
            addParameters("state", po.getPoEntity().getSupplier().getState());
            addParameters("postcode", po.getPoEntity().getSupplier().getPostCode());
            addParameters("country", po.getPoEntity().getSupplier().getCountry());
            addParameters("phone", po.getPoEntity().getSupplier().getPhone());
            addParameters("fax", po.getPoEntity().getSupplier().getFax());
        }
        Processor processor=new Processor(true);
        processor.useArial();
        if(po.getProjectEntity().getClient()!=null) {
            addParameters("clientName", po.getProjectEntity().getClient().getName().trim());
            Map detail= separateDetail(po.getProjectEntity().getClient().getTitle());

            addParameters("clientDetail1", detail.size() > 0 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(0).toString())) : null);
            processor.clear();
            addParameters("clientDetail2", detail.size() > 1 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(1).toString())) : null);
            processor.clear();
            addParameters("clientDetail3",detail.size()>2?Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(2).toString())):null);
        }
        addParameters("poNo",po.getPo());
        addParameters("poId",po.getPoEntity().getId());
        addParameters("orderDate",po.getPoEntity().getOrderDate());
        addParameters("deliveryDate",po.getPoDeliveryDate());
        addParameters("deliveryDateStr",po.getPoDeliveryDate()!=null?new java.text.SimpleDateFormat(configuration.getHardFormatDate(),new Locale("en")).format(org.joda.time.DateTimeZone.forID(configuration.getTimeZone()).convertUTCToLocal(po.getPoDeliveryDate().getTime())):"");
        addParameters("deliveryPoint",po.getPoEntity().getPoint());
        addParameters("deliveryInstructions",po.getPoEntity().getDeliveryInstruction());
        addParameters("procManager",po.getPoEntity().getProcManager());
        addParameters("procManagerDetail",po.getPoEntity().getProcManagerDetail());
        if(po.getPoEntity().getPoProcStatus().ordinal() != POStatusEnum.FINAL.ordinal()){
            InputStream watermark = resourceUtils.getResourceAsStream("/images/draft-report.jpg");
            log.info("InputStream watermark: " + watermark.toString());
            addParameters("watermarkDraft", watermark);
        }
        addParameters("poList",createDataSource(getPOReportDto()));
        addParameters("poTitle",po.getPoEntity().getOrderTitle());
        addParameters("projectName",po.getProjectEntity().getTitle());
        addParameters("projectNumber", po.getProjectEntity().getProjectNumber());
        addParameters("retentionApplicable", po.getPoEntity().getCashflow() != null ? BooleanUtils.toStringYesNo(po.getPoEntity().getCashflow().getApplyRetention()): "No");
        addParameters("liquidatedDamagesApplicable", BooleanUtils.toStringYesNo(po.getPoEntity().getLiquidatedDamagesApplicable()));
        addParameters("vendorDrawingData", BooleanUtils.toStringYesNo(po.getPoEntity().getVendorDrawingData()));
        addParameters("exchangeRateVariation", BooleanUtils.toStringYesNo(po.getPoEntity().getExchangeRateVariation()));
        addParameters("rtfNo",po.getPoEntity().getRTFNo());
        processor.clear();
        addParameters("invoiceTo", Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(po.getProjectEntity().getInvoiceTo())));
        addParameters("currency",po.getPoEntity().getCurrency()!=null&&po.getPoEntity().getCurrency().getCurrency()!=null? po.getPoEntity().getCurrency().getCurrency().getCode():null);
        addParameters("exchangeRate",po.getPoEntity().getCurrency().getCurrency()!=null? po.getPoEntity().getCurrency().getExchangeRate():null);


        Date now = new Date();
        addParameters("currentDate",Util.convertUTC(now,configuration.getTimeZone()));
    }
    private Map<Integer,String> separateDetail(String detail){
        Map<Integer,String> columns=new HashMap<>();
        String separator=System.lineSeparator()+System.lineSeparator();
        int i=0;
        for(String column:detail.split(separator)){
            if(StringUtils.isNotEmpty(column)&&StringUtils.isNotBlank(column)) {
                columns.put(i, column);
                i++;
            }
        }
        return columns;
    }

    private List<PurchaseOrderReportDto> getPOReportDto() {
        List<PurchaseOrderReportDto> dtos = new ArrayList<>();
        dtos.add(new PurchaseOrderReportDto(this.po,this.preamble));
        for(ScopeSupplyEntity entity : this.scopeSupplyList){
            PurchaseOrderReportDto dto = new PurchaseOrderReportDto(entity,po.getPoEntity().getCurrency());
            dtos.add(dto);
        }


        if(!scopeSupplyList.isEmpty()){
            PurchaseOrderReportDto poDto = new PurchaseOrderReportDto();
            dtos.add(poDto.loadTotalCost(po.getPoEntity().getCurrency().getCurrency().getCode(), getSumTotalCost()));
        }
        for(ClausesEntity entity : this.clausesList){
            PurchaseOrderReportDto dto = new PurchaseOrderReportDto(entity);
            dtos.add(dto);
        }
        return dtos;
    }

    private BigDecimal getSumTotalCost(){
        BigDecimal totalAmount = new BigDecimal("0.00000").setScale(5, RoundingMode.CEILING);
        for(ScopeSupplyEntity entity : this.scopeSupplyList){
            if(entity.getTotalCost()!=null){
                if(entity.getProjectCurrency()!=null){
                    totalAmount = totalAmount.add(Util.currencyToCurrency(entity.getTotalCost(),entity.getProjectCurrency().getExchangeRate(),po.getPoEntity().getCurrency().getExchangeRate()));
                }else{
                    totalAmount = totalAmount.add(entity.getTotalCost());
                }
            }
        }
        return totalAmount;
    }

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport(null);
        } catch (Exception ex) {

            if(!(ex.getMessage().contains("'&'")&&ex.getMessage().contains("org.xml.sax.SAXParseException;"))) {
                ex.printStackTrace();
            }else{
                log.log(Level.WARNING,"A special character is being used [&]");
            }
        }finally {
            try {
                if (connection != null && !connection.isClosed()) ;
                {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }




}

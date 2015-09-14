package ch.swissbytes.procurement.report;


import ch.swissbytes.Service.processor.Processor;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.report.dtos.PurchaseOrderReportDto;
import ch.swissbytes.procurement.report.dtos.PurchaseOrderSummaryDto;
import ch.swissbytes.procurement.util.ResourceUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    // private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private ResourceUtils resourceUtils;
    private PurchaseOrderEntity po;
    private List<ScopeSupplyEntity> scopeSupplyList;
    private String preamble;
    private List<ClausesEntity> clausesList;
    private Connection connection;
    private CashflowEntity cashflowEntity;
    private EntityManager entityManager;
    private int nivel = 1;
    private boolean draft;
    //private


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                               Configuration configuration, PurchaseOrderEntity po, List<ScopeSupplyEntity> scopeSupplyList,
                               String preamble, List<ClausesEntity> clausesList, CashflowEntity cashflowEntity, EntityManager entityManager,boolean draft) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.po = po;
        this.scopeSupplyList = scopeSupplyList;
        this.preamble = preamble;
        this.clausesList = clausesList;
        this.cashflowEntity = cashflowEntity;
        this.entityManager = entityManager;
        this.draft=draft;
        addParameters("patternDecimal", configuration.getPatternDecimal());
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("FORMAT_DATE2", configuration.getHardFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        addParameters("SUBREPORT_DIR", "reports/procurement/printPo/");
        try {
            connection = getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        addParameters("REPORT_CONNECTION", connection);
        loadParamPurchaseOrder();
        addParameters("paymentTerm", cashflowEntity != null && cashflowEntity.getPaymentTerms() != null ? cashflowEntity.getPaymentTerms().getLabel().toUpperCase() : null);
        addParameters("retentionApplicable", cashflowEntity != null && cashflowEntity.getApplyRetention() != null ? BooleanUtils.toStringYesNo(cashflowEntity.getApplyRetention()).toUpperCase() : "NO");
        addParameters("retentionForm", cashflowEntity != null && cashflowEntity.getForm() != null ? cashflowEntity.getForm().toUpperCase() : null);
    }

    private void loadParamPurchaseOrder() {
        resourceUtils = new ResourceUtils();
        loadParamLogos();
        addParameters("purchaseOrderId", po.getId());
        String variation = generateVariation(po.getVariationNumber());
        addParameters("variation", variation);
        loadParamSupplier();
        loadParamClients();
        Processor processor = new Processor(true);
        processor.useArial();
        addParameters("poNo", po.getPo());
        addParameters("poId", po.getPoEntity().getId());
        addParameters("orderDate", po.getPoEntity().getOrderDate());
        addParameters("deliveryDate", po.getPoDeliveryDate());
        addParameters("deliveryDateStr", po.getPoDeliveryDate() != null ? new java.text.SimpleDateFormat(configuration.getHardFormatDate(), new Locale("en")).format(org.joda.time.DateTimeZone.forID(configuration.getTimeZone()).convertUTCToLocal(po.getPoDeliveryDate().getTime())).toUpperCase() : "");
        addParameters("deliveryPoint", po.getPoEntity().getPoint() != null ? po.getPoEntity().getPoint().toUpperCase() : null);
        addParameters("deliveryInstructions", po.getPoEntity().getDeliveryInstruction());
        addParameters("procManager", po.getPoEntity().getProcManager());
        addParameters("procManagerDetail", po.getPoEntity().getProcManagerDetail());
      //  if (po.getPoEntity().getPoProcStatus().ordinal() != POStatusEnum.FINAL.ordinal()) {
        if(draft){
            InputStream watermark = resourceUtils.getResourceAsStream("/images/draft-report.jpg");
            log.info("InputStream watermark: " + watermark.toString());
            addParameters("watermarkDraft", watermark);
        }
        if (po.getOrderedVariation().intValue() == 1) {
            addParameters("isOriginal", true);
        } else if (po.getOrderedVariation().intValue() > 1) {
            addParameters("poSummaryList", createDataSource(getPOSummary()));
        }
        addParameters("poList", createDataSource(getPOReportDto()));
        addParameters("totalClauses", this.clausesList.size());
        addParameters("poTitle", po.getPoTitle());
        addParameters("projectName", po.getProjectEntity().getTitle());
        addParameters("projectNumber", po.getProjectEntity().getProjectNumber());

        addParameters("liquidatedDamagesApplicable", po.getPoEntity().getLiquidatedDamagesApplicable() != null ? BooleanUtils.toStringYesNo(po.getPoEntity().getLiquidatedDamagesApplicable()).toUpperCase() : null);
        addParameters("vendorDrawingData", po.getPoEntity().getVendorDrawingData() != null ? BooleanUtils.toStringYesNo(po.getPoEntity().getVendorDrawingData()).toUpperCase() : null);
        addParameters("exchangeRateVariation", po.getPoEntity().getExchangeRateVariation() != null ? BooleanUtils.toStringYesNo(po.getPoEntity().getExchangeRateVariation()).toUpperCase() : null);
        addParameters("rtfNo", po.getPoEntity().getRTFNo());
        addParameters("mrNo", collectMRNo());
        processor.clear();
        addParameters("invoiceTo", Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(po.getProjectEntity().getInvoiceTo())));
        addParameters("contactName", po.getPoEntity().getContactEntity() != null ? po.getPoEntity().getContactEntity().getFullName() : null);
        addParameters("contactEmail", po.getPoEntity().getContactEntity() != null ? po.getPoEntity().getContactEntity().getEmail() : null);
        addParameters("contactPhone", po.getPoEntity().getContactEntity() != null ? po.getPoEntity().getContactEntity().getPhone() : null);
        addParameters("contactFax", po.getPoEntity().getContactEntity() != null ? po.getPoEntity().getContactEntity().getFax() : null);

        int i = 1;
        Map<Long, String> currencies = getCurrenciesForPayment();
        for (Long key : currencies.keySet()) {
            if (i <= 3) {
                addParameters("currencyLbl" + i, currencies.get(key));
                addParameters("currencyId" + i, key);
            } else
                break;
            i++;
        }
        Date now = new Date();
        addParameters("currenciesNumber", currencies.size());
        addParameters("currentDate", Util.convertUTC(now, configuration.getTimeZone()));
    }

    private void loadParamLogos() {
        if (po.getProjectEntity().getClient() != null && po.getProjectEntity().getClient().getClientLogo() != null) {
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientLogo().getFile());
            addParameters("logo", logo);
        }

        if (po.getProjectEntity().getClient() != null && po.getProjectEntity().getClient().getClientLogoLeft() != null) {
            InputStream logo = new ByteArrayInputStream(po.getProjectEntity().getClient().getClientLogoLeft().getFile());
            addParameters("logoLeft", logo);
        }
    }

    private void loadParamSupplier() {
        if (po.getPoEntity().getSupplier() != null) {
            addParameters("company", po.getPoEntity().getSupplier().getCompany());
            addParameters("street", po.getPoEntity().getSupplier().getStreet());
            addParameters("state", po.getPoEntity().getSupplier().getState());
            addParameters("suburb", po.getPoEntity().getSupplier().getSuburb());
            addParameters("abnReg", po.getPoEntity().getSupplier().getAbnRegNo());
            addParameters("postcode", po.getPoEntity().getSupplier().getPostCode());
            addParameters("country", po.getPoEntity().getSupplier().getCountry());
            addParameters("phone", po.getPoEntity().getSupplier().getPhone());
            addParameters("fax", po.getPoEntity().getSupplier().getFax());
            addParameters("supplierId", po.getPoEntity().getSupplier().getId());
        }
    }

    private void loadParamClients() {
        Processor processor = new Processor(true);
        processor.useArial();
        if (po.getProjectEntity().getClient() != null) {
            addParameters("clientName", po.getProjectEntity().getClient().getName().trim());
            Map detail = separateDetail(po.getProjectEntity().getClient().getTitle());
            addParameters("clientDetail0", detail.size() > 0 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(0).toString())) : null);
            processor.clear();
            addParameters("clientDetail1", detail.size() > 1 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(1).toString())) : null);
            processor.clear();
            addParameters("clientDetail2", detail.size() > 2 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(2).toString())) : null);
            processor.clear();
            addParameters("clientDetail3", detail.size() > 3 ? Util.removeSpecialCharactersForJasperReport(processor.processSnippetText(detail.get(3).toString())) : null);
        }
    }

    private String collectMRNo() {
        StringBuilder sb = new StringBuilder();
        for (RequisitionEntity requisitionEntity : po.getPoEntity().getRequisitions()) {
            sb.append(requisitionEntity.getRequisitionNumber());
            sb.append(",");
        }
        Integer limit = sb.toString().length();
        if (sb.toString().length() > 0) {
            limit = limit - 1;
        }
        return sb.toString().substring(0, limit);
    }

    private String generateVariation(String variation) {
        String variationNumber = Util.removePrefixForVariation(variation, "v");
        return variationNumber != null && variationNumber.trim().length() > 0 && !variationNumber.trim().equalsIgnoreCase("0") ? variationNumber.trim() : null;
    }

    private Map<Integer, String> separateDetail(String detail) {
        Map<Integer, String> columns = new HashMap<>();
        String tableOpen = "<table>";
        String tableClose = "</table>";
        int index = detail.toLowerCase().indexOf(tableOpen);
        String separator = System.lineSeparator() + System.lineSeparator();
        int i = 0;
        String textToProcess = detail;
        if (index >= 0) {
            String firstText = StringUtils.chomp(detail.substring(0, index));
            String remainingText = StringUtils.chomp(detail.substring(index + tableOpen.length(), detail.length()));
            columns.put(i, firstText);
            i++;
            remainingText = remainingText.replaceAll(tableClose, "");
            remainingText = remainingText.replaceAll(tableOpen, "");
            textToProcess = remainingText;
            for (String column : textToProcess.split(separator)) {
                while (column.startsWith(System.lineSeparator()) && column.length() > 0) {
                    column = column.substring(1, column.length());
                }
                if (StringUtils.isNotEmpty(column) && StringUtils.isNotBlank(column)) {
                    columns.put(i, StringUtils.chomp(column));
                    i++;
                }
            }
        } else {
            columns.put(i, detail);
        }

        return columns;
    }

    private Map<Long, String> getCurrenciesForPayment() {
        Map<Long, String> currencies = new HashMap<Long, String>();
        if (cashflowEntity != null) {
            for (CashflowDetailEntity cashflowDetailEntity : cashflowEntity.getCashflowDetailList()) {
                if (cashflowDetailEntity.getProjectCurrency() != null) {
                    currencies.put(cashflowDetailEntity.getProjectCurrency().getId(), cashflowDetailEntity.getProjectCurrency().getCurrency().getSymbol() != null ? cashflowDetailEntity.getProjectCurrency().getCurrency().getSymbol() : cashflowDetailEntity.getProjectCurrency().getCurrency().getCode());
                }
            }
        }
        return currencies;
    }

    private List<PurchaseOrderReportDto> getPOReportDto() {
        List<PurchaseOrderReportDto> dtos = new ArrayList<>();
        dtos.add(new PurchaseOrderReportDto(""));
        dtos.add(new PurchaseOrderReportDto(po.getPoTitle()));
        dtos.add(new PurchaseOrderReportDto(null, this.preamble));
        for (ScopeSupplyEntity entity : this.scopeSupplyList) {
            PurchaseOrderReportDto dto = new PurchaseOrderReportDto(entity);
            dtos.add(dto);
        }
        return dtos;
    }

    private List<PurchaseOrderSummaryDto> getPOSummary() {
        log.info("preparing poSummary dto.......");
        List<PurchaseOrderSummaryDto> dtos = new ArrayList<>();
        List<Object> list = getSummaryItemsPO();
        if (po.getOrderedVariation().intValue() == 2) {
            log.info("Po is the first variation");
            addParameters("isOriginal", false);
            loadPOSummaryOriginal(list, dtos);
            loadThisVariation(list, dtos);
            loadRevisedOrderValue(list, dtos);
        } else if (po.getOrderedVariation().intValue() >= 3) {
            log.info("Po is the variation N");
            addParameters("isOriginal", false);
            loadPOSummaryOriginal(list, dtos);
            loadBetweenVariation(list, dtos);
            loadThisVariation(list, dtos);
            loadRevisedOrderValue(list, dtos);
        }

        return dtos;
    }

    private void loadBetweenVariation(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        List<Object> listAux = new ArrayList<>();
        listAux.addAll(list);
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitle("Variation No."+getDesriptionBetweenVariation());
        int turn = 1;
        int indexQuantity = 1;
        int quantityCurrencies = getQuantityCurrenciesUsedSummaryPO(2,po.getOrderedVariation()-1);
        for (Object record : list) {
            Object[] values = (Object[]) record;
            if (indexQuantity <= quantityCurrencies) {
                if (turn == 1) {
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode1((String) values[2]);
                    } else {
                        dto.setCurrencyCode1((String) values[1]);
                    }
                    BigInteger currencyId = (BigInteger) values[0];
                    BigDecimal total1 = new BigDecimal(0);
                    for (Object recordT1 : list) {
                        Object[] valuesT1 = (Object[]) recordT1;
                        if ((int) valuesT1[3] > 1 && (int) valuesT1[3] < po.getOrderedVariation().intValue()) {
                            if (currencyId.intValue() == ((BigInteger) valuesT1[0]).intValue()) {
                                total1 = total1.add((BigDecimal) valuesT1[4]);
                            }
                        }
                    }
                    dto.setAmount1(total1);
                }
                if (turn == 2) {
                    dto.setPlus1("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode2((String) values[2]);
                    } else {
                        dto.setCurrencyCode2((String) values[1]);
                    }
                    BigInteger currencyId = (BigInteger) values[0];
                    BigDecimal total1 = new BigDecimal(0);
                    for (Object recordT1 : list) {
                        Object[] valuesT1 = (Object[]) recordT1;
                        if ((int) valuesT1[3] > 1 && (int) valuesT1[3] < po.getOrderedVariation().intValue()) {
                            if (currencyId.intValue() == ((BigInteger) valuesT1[0]).intValue()) {
                                total1 = total1.add((BigDecimal) valuesT1[4]);
                            }
                        }
                    }
                    dto.setAmount2(total1);
                }
                if (turn == 3) {
                    dto.setPlus2("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode3((String) values[2]);
                    } else {
                        dto.setCurrencyCode3((String) values[1]);
                    }
                    BigInteger currencyId = (BigInteger) values[0];
                    BigDecimal total1 = new BigDecimal(0);
                    for (Object recordT1 : list) {
                        Object[] valuesT1 = (Object[]) recordT1;
                        if ((int) valuesT1[3] > 1 && (int) valuesT1[3] < po.getOrderedVariation().intValue()) {
                            if (currencyId.intValue() == ((BigInteger) valuesT1[0]).intValue()) {
                                total1 = total1.add((BigDecimal) valuesT1[4]);
                            }
                        }
                    }
                    dto.setAmount3(total1);
                }
                turn++;
            }
            indexQuantity++;
        }
        dtos.add(dto);
    }

    private void loadRevisedOrderValue(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        List<Object> listAux = new ArrayList<>();
        listAux.addAll(list);
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitle("Revised Order Value");

        String code1="";
        BigDecimal amt1 = new BigDecimal(0);
        boolean flagAmt1 = false;
        String code2="";
        BigDecimal amt2 = new BigDecimal(0);
        boolean flagAmt2 = false;
        String code3="";
        BigDecimal amt3 = new BigDecimal(0);
        boolean flagAmt3 = false;
        for(PurchaseOrderSummaryDto d : dtos){
            if(d.getAmount1() != null){
                flagAmt1 = true;
                amt1 = amt1.add(d.getAmount1());
            }
            if(d.getAmount2() != null){
                dto.setPlus1("plus");
                flagAmt2 = true;
                amt2 = amt2.add(d.getAmount2());
            }
            if(d.getAmount3() != null){
                dto.setPlus2("plus");
                flagAmt3 = true;
                amt3 = amt3.add(d.getAmount3());
            }
            if(StringUtils.isNotEmpty(d.getCurrencyCode1())){
                code1= d.getCurrencyCode1();
            }
            if(StringUtils.isNotEmpty(d.getCurrencyCode2())){
                code2= d.getCurrencyCode2();
            }
            if(StringUtils.isNotEmpty(d.getCurrencyCode3())){
                code3= d.getCurrencyCode3();
            }
        }
        dto.setCurrencyCode1(code1);
        dto.setCurrencyCode2(code2);
        dto.setCurrencyCode3(code3);
        dto.setAmount1(flagAmt1?amt1:null);
        dto.setAmount2(flagAmt2?amt2:null);
        dto.setAmount3(flagAmt3?amt3:null);
        dtos.add(dto);
    }

    private void loadThisVariation(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitle("This Variation No." + po.getVariation());
        int turn = 1;
        for (Object record : list) {
            Object[] values = (Object[]) record;
            if ((int) values[3] == po.getOrderedVariation()) {
                if (turn == 1) {
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode1((String) values[2]);
                    } else {
                        dto.setCurrencyCode1((String) values[1]);
                    }
                    dto.setAmount1((BigDecimal) values[4]);
                }
                if (turn == 2) {
                    dto.setPlus1("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode2((String) values[2]);
                    } else {
                        dto.setCurrencyCode2((String) values[1]);
                    }
                    dto.setAmount2((BigDecimal) values[4]);
                }
                if (turn == 3) {
                    dto.setPlus2("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode3((String) values[2]);
                    } else {
                        dto.setCurrencyCode3((String) values[1]);
                    }
                    dto.setAmount3((BigDecimal) values[4]);
                }
                turn++;
            }
        }
        dtos.add(dto);
    }

    private void loadPOSummaryOriginal(final List<Object> list, List<PurchaseOrderSummaryDto> dtos) {
        PurchaseOrderSummaryDto dto = new PurchaseOrderSummaryDto();
        dto.setTitle("Original Order Value");
        int turn = 1;
        for (Object record : list) {
            Object[] values = (Object[]) record;
            if ((int) values[3] == 1) {
                if (turn == 1) {
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode1((String) values[2]);
                    } else {
                        dto.setCurrencyCode1((String) values[1]);
                    }
                    dto.setAmount1((BigDecimal) values[4]);
                }
                if (turn == 2) {
                    dto.setPlus1("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode2((String) values[2]);
                    } else {
                        dto.setCurrencyCode2((String) values[1]);
                    }
                    dto.setAmount2((BigDecimal) values[4]);
                }
                if (turn == 3) {
                    dto.setPlus2("plus");
                    if (values[2] != null && StringUtils.isNotEmpty((String) values[2])) {
                        dto.setCurrencyCode3((String) values[2]);
                    } else {
                        dto.setCurrencyCode3((String) values[1]);
                    }
                    dto.setAmount3((BigDecimal) values[4]);
                }
                turn++;
            }
        }
        dtos.add(dto);
    }

    private List<Object> getSummaryItemsPO() {
        Query query = entityManager.createNativeQuery("select cu.id,cu.code,cu.symbol,po.orderedvariation,sum (total_cost),po.variation\n" +
                "from scope_supply sp inner join purchase_order po  on sp.purchase_order_id= po.id\n" +
                "left join project_currency pc on pc.id= sp.project_currency_id\n" +
                "inner join currency cu on pc.currency_id=cu.id\n" +
                "where sp.status_id=1 and po.po = '" + po.getPo() + "' and po.project_id=" + po.getProjectEntity().getId() + "\n" +
                "group by cu.id,cu.code,cu.symbol,po.orderedvariation,po.variation\n" +
                "order by po.orderedvariation");
        return query.getResultList();
    }

    private int getQuantityCurrenciesUsedSummaryPO(Integer orderStart, Integer orderEnd) {
        Query query = entityManager.createNativeQuery("select cu.id\n" +
                "from scope_supply sp inner join purchase_order po  on sp.purchase_order_id= po.id\n" +
                "left join project_currency pc on pc.id= sp.project_currency_id\n" +
                "inner join currency cu on pc.currency_id=cu.id\n" +
                "where sp.status_id=1 and po.po = '" + po.getPo() + "' and po.project_id= " + po.getProjectEntity().getId() + " and orderedvariation between " + orderStart + " and " + orderEnd + "\n" +
                "group by cu.id\n" +
                "order by cu.id");
        return query.getResultList().size();
    }

    private List<Object> getCurrenciesUsedSummaryPO(Integer orderStart, Integer orderEnd) {
        Query query = entityManager.createNativeQuery("select cu.id\n" +
                "from scope_supply sp inner join purchase_order po  on sp.purchase_order_id= po.id\n" +
                "left join project_currency pc on pc.id= sp.project_currency_id\n" +
                "inner join currency cu on pc.currency_id=cu.id\n" +
                "where sp.status_id=1 and po.po = '" + po.getPo() + "' and po.project_id= " + po.getProjectEntity().getId() + " and orderedvariation between " + orderStart + " and " + orderEnd + "\n" +
                "group by cu.id\n" +
                "order by cu.id");
        return query.getResultList();
    }

    private String getDesriptionBetweenVariation() {
        Long poMinId = -1L;
        Long poMaxId = -1L;
        String result = "";
        Query query = entityManager.createNativeQuery("select distinct po.orderedvariation, po.id\n" +
                "from scope_supply sp inner join purchase_order po  on sp.purchase_order_id= po.id\n" +
                "left join project_currency pc on pc.id= sp.project_currency_id\n" +
                "inner join currency cu on pc.currency_id=cu.id\n" +
                "where sp.status_id=1 and po.po = '" + po.getPo() + "' and po.project_id=" + po.getProjectEntity().getId() + "\n" +
                "order by po.orderedvariation");
        List<Object> list = query.getResultList();
        for (Object record : list) {
            Object[] values = (Object[]) record;
            if ((int) values[0] == 2) {
                poMinId = ((BigInteger) values[1]).longValue();
            }
            if ((int) values[0] == po.getOrderedVariation() - 1) {
                poMaxId = ((BigInteger) values[1]).longValue();
            }
        }

        if(poMinId.longValue() != poMaxId.longValue()){
            PurchaseOrderEntity poMin =  findPurchaseOrderById(poMinId);
            PurchaseOrderEntity poMax =  findPurchaseOrderById(poMaxId);
            result = "v"+poMin.getVariation() +" - v"+ poMax.getVariation();
        }else if(poMinId.longValue() == poMaxId.longValue()){
            PurchaseOrderEntity poMin =  findPurchaseOrderById(poMinId);
            result = poMin.getVariation();
        }
        return result;
    }

    private PurchaseOrderEntity findPurchaseOrderById(final Long id ){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM PurchaseOrderEntity p ");
        sb.append(" WHERE p.id = :ID ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("ID",id);
        List<PurchaseOrderEntity> list = query.getResultList();
        return list.get(0);
    }



    @Override
    public void printDocument(Long documentId) {
        try {
            runReport(null);
        } catch (Exception ex) {

            if (!(ex.getMessage().contains("'&'") && ex.getMessage().contains("org.xml.sax.SAXParseException;"))) {
                ex.printStackTrace();
            } else {
                log.log(Level.WARNING, "A special character is being used [&]");
            }
        } finally {
            try {
                if (connection != null && !connection.isClosed()) ;
                {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

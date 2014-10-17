package ch.swissbytes.fqmes.report;



import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.LookupValueFactory;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportPurchaseOrder extends ReportView implements Serializable {


    private EntityManager entityManager;

    private final Logger log = Logger.getLogger(ReportPurchaseOrder.class.getName());

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");


    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPurchaseOrder(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale, EntityManager entityManager, final List<PurchaseOrderEntity> orders) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.entityManager=entityManager;
         Collection<Long>ids=new ArrayList<>();
        for(PurchaseOrderEntity entity:orders ){
            ids.add(entity.getId());
        }
        if(ids.size()>0){
            addParameters("purchaseID",ids);
        }
        addParameters("SUBREPORT_DIR","reports/jobSummary/");
        LookupValueFactory lookupValueFactory=new LookupValueFactory();
        addParameters("TIME_MEASUREMENT",lookupValueFactory.geTimesMeasurement());

    }

    @Override
    public void printDocument(Long documentId) {
        try{
            runReport();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    /*@Override
    public void printDocument(Long documentId) {
        log.info("void printDocument(Long documentId)");
        customerService = new CustomerService(entityManager);
        List<Customer> clients =  customerService.findAll();
        List<ClientReportDto> clientsDto = getClientsDto(clients);
        initalizeHeader();
        initializeHeaderColumn();
        runReport(clientsDto);
    }*/

   /*private void initalizeHeader() {
        ConfigurationService configurationService = new ConfigurationService();
        addParameters("titleReportLbl", bundle.getString("TITLE_REPORT"));
        addParameters("titleCompanyLbl",bundle.getString("TITLE_COMPANY"));
        addParameters("titleReport",bundle.getString("report_client_title"));
        addParameters("titleCompany",configurationService.getConfigurationValue("NAME_COMPANY",entityManager));
        try{
            addParameters("logo", getLogoAsStream(configurationService.getConfigurationValue("CABEXSE_LOGO",entityManager)));
        }catch (Exception e){
            e.printStackTrace();
        }

        addParameters("formatDate", configurationService.getConfigurationValue("FORMAT_DATE",entityManager));
        addParameters("formatTime", configurationService.getConfigurationValue("FORMAT_TIME",entityManager));
        addParameters("formatLanguage", configurationService.getConfigurationValue("FORMAT_LANGUAGE",entityManager));
        addParameters("formatCountry", configurationService.getConfigurationValue("FORMAT_COUNTRY",entityManager));
        addParameters("pageLbl", bundle.getString("PAG_REPORT"));
        addParameters("ofLbl", bundle.getString("OF_REPORT"));
        addParameters("currentDate",new Date());
        //String currentDate = DateFormatUtils.format(new Date(), "HH:mm", new Locale("es", "BO"));
    }

    private void initializeHeaderColumn() {
        addParameters("nroColLbl", bundle.getString("report_nro_col"));
        addParameters("grsnColLbl",bundle.getString("report_grsn_col"));
        addParameters("clientColLbl",bundle.getString("report_client_name_col"));
        addParameters("categoryColLbl",bundle.getString("report_category_col"));
        addParameters("baseColLbl", bundle.getString("report_base_col"));
        addParameters("nitColLbl", bundle.getString("report_nit_col"));
        addParameters("emailColLbl", bundle.getString("report_email_col"));
        addParameters("telephoneFaxColLbl", bundle.getString("report_phone_col"));
        addParameters("webColLbl", bundle.getString("report_web_col"));
        addParameters("contactColLbl", bundle.getString("report_contact_col"));
        addParameters("faxColLbl", bundle.getString("report_fax_col"));
    }

    private List<ClientReportDto> getClientsDto(List<Customer> clients) {
        List<ClientReportDto> clientDto = new ArrayList<ClientReportDto>();
        int nro = 1;
        for(Customer c : clients){
            ClientReportDto dto = new ClientReportDto();
            dto.copyToDto(nro,c);
            clientDto.add(dto);
            nro++;
        }
            return clientDto;
    }*/
}

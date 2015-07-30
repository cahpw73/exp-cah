package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by alvaro on 25/06/15.
 */
public class ReportBidderList extends ReportView implements Serializable {

    private final Logger log = Logger.getLogger(ReportBidderList.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    private Configuration configuration;
    private List<Long>suppliers;
    private String packageNumber;
    private String description;
    private String comments;
   // private String codeProject;
    private ProjectEntity project;



    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link Locale}
     */
    public ReportBidderList(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                            Configuration configuration, List<Long>suppliers,String packageNumber,String description,String comments,ProjectEntity project) {
        super(filenameJasper, reportNameMsgKey, messages, locale);
        this.configuration = configuration;
        this.suppliers=suppliers;
        this.packageNumber=packageNumber;
        this.description=description;
        this.comments=comments;
       // this.codeProject=codeProject;
        this.project=project;
        addParameters("FORMAT_DATE", configuration.getFormatDate());
        addParameters("TIME_ZONE", configuration.getTimeZone());
        loadParameters();
    }

    private void loadParameters() {
        if(suppliers==null||suppliers.isEmpty()){
            suppliers=new ArrayList<>();
            suppliers.add(0L);
        }
        if(project.getClient()!=null && project.getClient().getClientLogo()!=null){
            InputStream logo = new ByteArrayInputStream(project.getClient().getClientLogo().getFile());
            addParameters("logo", logo);
        }else if(project.getClient()!=null && project.getClient().getDefaultLogo()!=null){
            InputStream logo = new ByteArrayInputStream(project.getDefaultLogo().getFile());
            addParameters("logo", logo);
        }
        addParameters("supplier",suppliers);
        addParameters("packageNumber", packageNumber);
        addParameters("description",description);
        addParameters("comments", comments);
        addParameters("projectCode", project.getProjectNumber());
        addParameters("project", project.getTitle());
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

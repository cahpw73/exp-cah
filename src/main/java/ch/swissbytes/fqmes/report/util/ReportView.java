package ch.swissbytes.fqmes.report.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Geremias Gonzalez
 */
public abstract class ReportView implements Serializable {

    private DocTypeEnum reportType = DocTypeEnum.PDF;

    private final Map<String, String> messages;

    private final String filenameJasper;

    private final StringBuilder reportName;

    private final Map<String, Object> parameters = new HashMap<String, Object>();

    private final String DATA_SOURCE="java:/fqm/fqmesDS";

    /***
     *
     * @param filenameJasper - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale {@link java.util.Locale}
     */
    public ReportView( final String filenameJasper, final String reportNameMsgKey, final Map<String, String> messages, final Locale locale) {
        this.messages = messages;
        this.filenameJasper = filenameJasper;
        this.reportName = new StringBuilder("reports.pallet");
        parameters.put(JRParameter.REPORT_LOCALE, locale);
    }

    public abstract void printDocument(Long documentId);

    public void printDocument(DocTypeEnum reportType, Long documentId) {
        setReportType(reportType);
        printDocument(documentId);
    }


    protected void runReport(final List<?> beanCollection) {

        FacesContext fcontext = FacesContext.getCurrentInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {

            HttpServletResponse response = (HttpServletResponse) fcontext.getExternalContext().getResponse();
            final JasperPrint jasperPrint = JasperFillManager.fillReport(ReportFileUtils.loadReport(filenameJasper), parameters, createDataSource((beanCollection)));
            exportReport(jasperPrint, outputStream, response, reportName.toString());

            outputStream.writeTo(response.getOutputStream());
            response.setContentLength(outputStream.size());
        } catch (JRException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex){
          ex.printStackTrace();
        }   finally{
            IOUtils.closeQuietly(outputStream);
        }

        fcontext.responseComplete();
    }


    protected void runReport() throws Exception {
        System.out.println("runReport()");
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Connection connection = null;

        try {
            connection = getDataSource().getConnection();
            HttpServletResponse response = (HttpServletResponse) fcontext.getExternalContext().getResponse();
            final JasperPrint jasperPrint = JasperFillManager.fillReport(ReportFileUtils.loadReport(filenameJasper), parameters, connection);
            exportReport(jasperPrint, outputStream, response, reportName.toString());
            outputStream.writeTo(response.getOutputStream());
            response.setContentLength(outputStream.size());
        } catch (SQLException e) {
           // LOG.error(" Database access error!.", e);
            e.printStackTrace();
        } catch (JRException e) {
            //LOG.error(" Error to the fill the reports!.", e);
            e.printStackTrace();
        } catch (Exception e) {
            //LOG.error(" Unknown error!.", e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        fcontext.responseComplete();
    }

    protected JRDataSource createDataSource(Collection<?> beanCollection) {
        return (beanCollection == null || beanCollection.isEmpty()) ? new JREmptyDataSource() : new JRBeanCollectionDataSource(beanCollection);
    }

    protected InputStream getLogoAsStream(String nameImage) throws IOException {
        return ReportFileUtils.getImageAsStream(nameImage);
    }

    protected InputStream getImgQr() throws IOException {
        return ReportFileUtils.getImageAsStream("incompleteQrCode.png");
    }


    public void addWordToReportName(String word) {
        if (StringUtils.isNotBlank(word)) {
            reportName.append("-");
            reportName.append(word);
        }
    }

    private DataSource getDataSource() {
        try {
            InitialContext ctx = new InitialContext();
            return (DataSource) ctx.lookup(DATA_SOURCE);
        } catch (Exception e) {
            throw new RuntimeException("Datasource not accessible: " + e, e);
        }
    }

    private void exportReport(final JasperPrint jasperPrint, final OutputStream outputStream, final HttpServletResponse response, final String reportName) throws JRException {
        JRAbstractExporter exporter = ExporterReportUtils.crateExporterReportAction(reportType, jasperPrint, outputStream, response, reportName);
        exporter.exportReport();
    }

    protected void addParameters(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            parameters.put(key, value);
        }
    }

    public DocTypeEnum getReportType() {
        return reportType;
    }

    public void setReportType(DocTypeEnum reportType) {
        this.reportType = reportType;
    }

    public String getMessage(final String key) {
        return messages.get(key);
    }

    public String getMessage(final String parentKey, final Enum<?> type) {
        return type != null ? getMessage(parentKey, type.name()) : null;
    }

    public String getMessage(final String parentKey, final Enum<?> type, String messageKeyDefault) {
        return type != null ? getMessage(parentKey, type.name()) : getMessage(messageKeyDefault);
    }

    public String getMessage(final String parentKey, final String key) {
        return messages.get(parentKey + "." + key);
    }
}

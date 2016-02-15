package ch.swissbytes.fqmes.report.util;

import ch.swissbytes.procurement.util.XmlWorker;
import com.itextpdf.text.DocumentException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXParseException;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLClientInfoException;
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

    private final String DATA_SOURCE = "java:/fqm/procurementDS";

    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportView(final String filenameJasper, final String reportNameMsgKey, final Map<String, String> messages, final Locale locale) {
        this.messages = messages;
        this.filenameJasper = filenameJasper;
        this.reportName = new StringBuilder(reportNameMsgKey);
        parameters.put(JRParameter.REPORT_LOCALE, locale);
    }

    public abstract void printDocument(Long documentId);

    public void printDocument(DocTypeEnum reportType, Long documentId) {
        setReportType(reportType);
        printDocument(documentId);
    }

    protected Connection getConnection()throws SQLClientInfoException,JRException, Exception{
        return getDataSource().getConnection();
    }

    protected void runReport(final List<?> beanCollection) {

        FacesContext fcontext = FacesContext.getCurrentInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {

            HttpServletResponse response = (HttpServletResponse) fcontext.getExternalContext().getResponse();
            final JasperPrint jasperPrint = JasperFillManager.fillReport(ReportFileUtils.loadReport(filenameJasper), parameters, createDataSource((beanCollection)));
            exportReport(jasperPrint, outputStream, response, getOnlyReportNameFormat(reportName.toString()));

            outputStream.writeTo(response.getOutputStream());
            response.setContentLength(outputStream.size());
        } catch (JRException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        fcontext.responseComplete();
    }

    protected void runReport() throws Exception {
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            HttpServletResponse response = (HttpServletResponse) fcontext.getExternalContext().getResponse();
            final JasperPrint jasperPrint = JasperFillManager.fillReport(ReportFileUtils.loadReport(filenameJasper), parameters, connection);
            exportReport(jasperPrint, outputStream, response, getOnlyReportNameFormat(reportName.toString()));
            outputStream.writeTo(response.getOutputStream());
            response.setContentLength(outputStream.size());
        } catch (SQLException e) {
            // LOG.error(" Database access error!.", e);
            e.printStackTrace();
        } catch (JRException e) {
            //LOG.error(" Error to the fill the reports!.", e);
            e.printStackTrace();
        } catch (Exception e) {
            //LOG.error(" Unknown error!.", e);r
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        fcontext.responseComplete();
    }

    protected void runReport(final List<?> beanCollection,byte[] outputStreamDoc) {
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XmlWorker xmlWorker = new XmlWorker();
        try {
            HttpServletResponse response = (HttpServletResponse) fcontext.getExternalContext().getResponse();
            final JasperPrint jasperPrint = JasperFillManager.fillReport(ReportFileUtils.loadReport(filenameJasper), parameters, createDataSource((beanCollection)));
            exportReport(jasperPrint, outputStream, response, getOnlyReportNameFormat(reportName.toString()));
            xmlWorker.manipulatePdf(outputStream.toByteArray(), outputStreamDoc).writeTo(response.getOutputStream());
            response.setContentLength(outputStream.size());
        } catch (JRException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }  catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        fcontext.responseComplete();
    }

    private static String getOnlyReportNameFormat(final String reportName) {
        String fileFormat = reportName;
        if (StringUtils.isEmpty(fileFormat)) {
            fileFormat = "report";

        } else {

            // For filename report (jasperreports)
          //  fileFormat = fileFormat.replace(' ', '-');

            // For windows -> filename
            fileFormat = fileFormat.replace(',', ' ');
            fileFormat = fileFormat.replace('\\', ' ');
            fileFormat = fileFormat.replace('/', ' ');
            fileFormat = fileFormat.replace(':', ' ');
            fileFormat = fileFormat.replace('*', ' ');
            fileFormat = fileFormat.replace('?', ' ');
            fileFormat = fileFormat.replace('\"', ' ');
            fileFormat = fileFormat.replace('<', ' ');
            fileFormat = fileFormat.replace('>', ' ');
            fileFormat = fileFormat.replace('|', ' ');
        }
        return fileFormat;
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

package ch.swissbytes.procurement.util;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Created by Christian on 20/07/2015.
 */
public class SpreadsheetProcessor implements Serializable {

    //Create blank workbook
    private XSSFWorkbook workbook;

    //Create a blank sheet
    private XSSFSheet spreadsheet;

    //Create row object
    private XSSFRow row;

    public void createWorkbook() {
        workbook = new XSSFWorkbook();
    }

    public void createSpreadsheet(String name) {
        spreadsheet = workbook.createSheet(name);
    }

    public void createRow(int rowNo) {
        row = spreadsheet.createRow((short) rowNo);
    }

    public void writeStringValue(int colNo, String value) {
        row.createCell(colNo).setCellValue(value);
    }

    public void writeDoubleValue(int colNo, Double value) {
        row.createCell(colNo).setCellValue(value == null ? "" : value.toString());
    }

    public void doSaveWorkBook(final String path, final String fileName) {
        FileOutputStream out = null;
        File file = createDirectoryFiles(path);
        File newFile = new File(file.getAbsolutePath()+File.separator+fileName);
        try {
            if(newFile.createNewFile()){
                System.out.println("created file");
            }
            out = new FileOutputStream(newFile);
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createDirectoryFiles(String path) {
        File theDir = new File(path);
        if (!theDir.exists()) {
            try {
                theDir.mkdirs();
            } catch (SecurityException se) {

            }
        }
        return theDir;
    }

    public ByteArrayOutputStream saveWorkBook() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public InputStream getContentSheet() {
        ByteArrayOutputStream output = saveWorkBook();
        if (output != null) {
            return new ByteArrayInputStream(output.toByteArray());
        }
        return null;
    }

}

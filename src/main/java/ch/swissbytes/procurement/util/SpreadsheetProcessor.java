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
        row.createCell(colNo).setCellValue(value);
    }

    public void saveWorkBook(String fileName) {
        FileOutputStream out = null;
        try {

            FileInputStream fi=new FileInputStream("");
            out = new FileOutputStream(new File(fileName + ".xlsx"));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SpreadsheetProcessor sp = new SpreadsheetProcessor();
        sp.createWorkbook();
        sp.createSpreadsheet("PurchaseOrder");
        sp.createRow(0);
        sp.writeStringValue(0, "45");
        sp.writeDoubleValue(1, 45.45987D);

        sp.createRow(1);
        sp.writeStringValue(1, "Puesto?" );
        sp.writeDoubleValue(0, 10D);

        sp.createSpreadsheet("Purchase Detail");

        sp.createRow(0);
        sp.writeStringValue(1, "Detalle?");
        sp.writeDoubleValue(0, 10D);


        sp.saveWorkBook("PRUEBA");
        System.out.println("written successfully");
    }

}

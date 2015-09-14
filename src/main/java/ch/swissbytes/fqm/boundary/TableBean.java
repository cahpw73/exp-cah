package ch.swissbytes.fqm.boundary;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by alvaro on 9/14/2015.
 */
@Named
@SessionScoped
public class TableBean implements Serializable{

    private String defaultPageSize="5";

    public void onPaginate(PageEvent event){
        DataTable table=(DataTable)event.getSource();
        defaultPageSize=Integer.toString(table.getRowCount());
    }

    public String getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(String defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
}

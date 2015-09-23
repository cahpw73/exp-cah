package ch.swissbytes.procurement.boundary;

import ch.swissbytes.Service.infrastructure.Filter;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 9/22/2015.
 */
public abstract class ManagerTable implements Serializable{

    protected Long lastEdited;
    protected Boolean remember;
    private Integer currentPage=0;
    protected Filter filter;
    private List <String>ascendingFields;
    private List<String>descendingFields;
    private Integer defaultPageSize;


    protected abstract Integer findCurrentPage();

    public ManagerTable(){
        clear();
    }
    protected void clear(){
        ascendingFields=new ArrayList<>();
        descendingFields=new ArrayList<>();
        lastEdited=-1L;
        remember=false;
    }


    public void onPaginate(PageEvent event){
        DataTable table=(DataTable)event.getSource();
        defaultPageSize=table.getRowCount();
        currentPage=table.getPage();
    }


    public Long getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Long lastEdited) {
        this.lastEdited = lastEdited;
    }

    public Boolean getRemember() {
        return remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<String> getAscendingFields() {
        return ascendingFields;
    }

    public List<String> getDescendingFields() {
        return descendingFields;
    }

    public Integer getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
}

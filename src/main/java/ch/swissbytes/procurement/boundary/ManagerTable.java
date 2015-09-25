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
    private final Integer PAGE_ZERO=0;
    private Integer currentPage=PAGE_ZERO;
    protected Filter filter;
    private List <String>ascendingFields;
    private List<String>descendingFields;
    private Integer defaultPageSize;


    protected abstract Integer findCurrentPage();

    public ManagerTable(){
        clear();
    }

    public void clear(){
        ascendingFields=new ArrayList<>();
        descendingFields=new ArrayList<>();
        lastEdited=-1L;
        remember=false;
        filter=new Filter();
    }


    public void onPaginate(PageEvent event){
        forgetCurrentPage();
        DataTable table=(DataTable)event.getSource();
        defaultPageSize=table.getRowCount();
        currentPage=event.getPage();
    }

    public Integer pageToSelect(){
        return remember?currentPage:0;
    }

    public void forgetCurrentPage(){
        remember=false;
    }
    public void rememberPage(){
        remember=true;
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

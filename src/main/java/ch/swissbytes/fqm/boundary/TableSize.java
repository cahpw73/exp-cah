package ch.swissbytes.fqm.boundary;


import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by alvaro on 9/14/2015.
 */
@Named
@SessionScoped
public class TableSize  implements Serializable{

    private Integer defaultPageSize=5;


    public Integer getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
}

package ch.swissbytes.procurement.boundary.report.bidList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 6/19/2015.
 */
//@Named
//@ViewScoped
public class BidListBean {

    public static final Logger log = Logger.getLogger(BidListBean.class.getName());

    private Long projectId;

    @PostConstruct
    public void create(){
        log.log(Level.FINE,"creating bidListBean");
    }

    @PreDestroy
    public void destroy(){
        log.log(Level.FINE,"destroying bidListBean");
    }

}

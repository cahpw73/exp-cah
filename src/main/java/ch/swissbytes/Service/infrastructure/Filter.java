package ch.swissbytes.Service.infrastructure;

/**
 * Created by alvaro on 9/15/14.
 */
public  class Filter {

    private String criteria;

    public void  clean(){
        criteria=null;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}

package ch.swissbytes.domain.interfaces;

/**
 * Created by alvaro on 9/15/14.
 */
public class Record {

    protected boolean withNoData =false;

    public boolean isWithNoData() {
        return withNoData;
    }

    public void setWithNoData(boolean withNoData) {
        this.withNoData = withNoData;
    }
}

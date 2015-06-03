package ch.swissbytes.domain.interfaces;

import org.apache.commons.beanutils.BeanUtils;

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

package ch.swissbytes.domain.interfaces;


import org.apache.commons.beanutils.BeanUtils;


import java.io.Serializable;

import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
public class RecordEditable<T> implements Serializable {

    public final static Logger log = Logger.getLogger(RecordEditable.class.getName());

    private Boolean isEditable;

    private T valueCloned;

    public void stopEditing(){
        log.info("stopEditing");
        isEditable = false;
    }

    public void startEditing(){
        log.info("startEditing");
        isEditable = true;
    }

    public void storeOldValue(T oldValue) {
        try {
            BeanUtils.copyProperties(oldValue,valueCloned);
           // valueCloned=BeanUtils.cloneBean(oldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }


    public Object getValueCloned() {
        return valueCloned;
    }

}

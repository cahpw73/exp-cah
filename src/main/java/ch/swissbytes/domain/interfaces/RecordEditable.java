package ch.swissbytes.domain.interfaces;


import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.beanutils.BeanUtils;


import javax.persistence.Transient;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transient
    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }


    @Transient
    public Object getValueCloned() {
        return valueCloned;
    }

    public void setId(Long id){

    }

    public Long getId(){
        return 0L;
    }

    public void setStatus(StatusEnum status){

    }

    public StatusEnum getStatus(){
        return null;
    }

}

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

    private Boolean isEditable=false;

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
            //BeanUtils.copyProperties(valueCloned,oldValue);
            valueCloned=(T)BeanUtils.cloneBean(oldValue);
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
    public T getValueCloned() {
        return valueCloned;
    }

    public void setValueCloned(T value){
        valueCloned=value;
    }

    public void setId(Long id){

    }

    public Long getId(){
        return 0L;
    }

    public void setStatusEnum(StatusEnum status){

    }

    public StatusEnum getStatusEnum(){
        return null;
    }

}

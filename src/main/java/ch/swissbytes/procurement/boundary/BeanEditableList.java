package ch.swissbytes.procurement.boundary;

import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.types.StatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 04-06-15.
 */
public class BeanEditableList<T> extends Bean {

    protected List<T> list = new ArrayList<>();

    private Long temporaryId = -1L;

    public BeanEditableList() {
        super();
    }

    public void add(T element) {
        if (canEdit()) {
            RecordEditable record = (RecordEditable) element;
            record.setId(temporaryId);
            temporaryId--;
            putModeCreation();
            record.setStatusEnum(StatusEnum.ENABLE);
            record.startEditing();
            list.add((T) record);
        }
    }


    public void edit(Long id) {
        if (canEdit()) {
            RecordEditable record = find(id);
            if (record != null) {
                putModeEdition();
                record.storeOldValue(record);
                record.startEditing();
            }
        }

    }

    private RecordEditable find(Long id) {
        RecordEditable recordEditable = null;
        for (T re : list) {
            RecordEditable r = (RecordEditable) re;
            if (r.getId().longValue() == id.longValue()) {
                recordEditable = r;
                break;
            }
        }
        return recordEditable;
    }

    private Integer index(Long id) {
        int index = -1;
        int i = 0;
        for (T re : list) {
            RecordEditable r = (RecordEditable) re;
            if (r.getId().longValue() == id.longValue()) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }

    public void confirm(Long id) {
        RecordEditable record = find(id);
        if (record != null) {
            if (validate(record)) {
                record.stopEditing();
            }
        }
    }

    protected boolean canEdit() {
        return true;
    }

    public void cancel(Long id) {
        int index = index(id);
        if (index >= 0) {
            if (isBeingCreated()) {
                list.remove(index);
            }
            if (isBeingUpdated()) {
                T originalValue = (T) ((RecordEditable) list.get(index)).getValueCloned();
                list.set(index, originalValue);
            }
        }
        putModeView();
    }

    public void delete(Long id) {
        RecordEditable record = find(id);
        record.setStatusEnum(StatusEnum.DELETED);
    }

    protected boolean validate(RecordEditable record) {
        return true;
    }

    public List<T> filteredList() {
        List<T> list = new ArrayList<>();
        for (T r : this.list) {
            RecordEditable record = (RecordEditable) r;
            if (record.getStatusEnum() != null && record.getStatusEnum().getId().intValue() == StatusEnum.ENABLE.getId().intValue()) {
                T object = (T) record;
                list.add(object);
            }

        }
        return list;
    }

    public Integer rowsBeingEdited() {
        Integer rows = 0;
        for (T r : list) {
            RecordEditable record = (RecordEditable) r;
            if (record.getIsEditable()) {
                rows++;
            }
        }
        return rows;
    }

    public List<T> getList() {
        return list;
    }

    public void reset(){
        list=new ArrayList<>();
    }
}

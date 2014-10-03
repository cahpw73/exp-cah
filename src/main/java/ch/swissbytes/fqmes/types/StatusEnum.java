package ch.swissbytes.fqmes.types;

import ch.swissbytes.fqmes.model.entities.StatusEntity;

import java.io.Serializable;

public enum StatusEnum implements FqmEnum<StatusEnum>, Serializable  {

    ENABLE("Active",1), DISABLED("Inactive",2), DELETED("Deleted",3);

    private String label;
    private Integer id;

    StatusEnum(String label, Integer id){
        this.label = label;
        this.id=id;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }


    public boolean equalsTo(final StatusEnum stEnum){
        return stEnum != null ? (stEnum.getId().intValue() == this.id.intValue()) : false;
    }

    public boolean equalsTo(final Integer id){
        return id != null ? (id.intValue() == this.id.intValue()) : false;
    }

    public static StatusEnum of(final StatusEntity status) {
        return valueOf(status.getName());
    }

    public static StatusEnum getStatusEnum(final String statusLabel) {
        StatusEnum statusEnum = null;
        statusEnum = valueOf(statusLabel);
        return statusEnum;
    }

    public static StatusEnum valueOf(Integer id) {
        if (id!=null) {
            for (StatusEnum item : values()) {
                if (item.id.equals(id)) {
                    return item;
                }
            }
        }
        throw new IllegalArgumentException("Client Status Id invalid : [" + id + "]");
    }


}

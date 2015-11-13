package ch.swissbytes.domain.types;

import ch.swissbytes.domain.model.entities.StatusEntity;

import java.io.Serializable;

public enum ClassEnum implements FqmEnum<ClassEnum>, Serializable  {

    PO("PO",0),
    SERVICE_CONTRACT("Service Contract",1),
    CONSTRUCTION_CONTRACT("Construction Contract",2),
    MINING_FLEET("Mining Fleet",3);

    private String label;
    private Integer id;

    ClassEnum(String label, Integer id){
        this.label = label;
        this.id=id;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }


    public boolean equalsTo(final ClassEnum stEnum){
        return stEnum != null ? (stEnum.getId().intValue() == this.id.intValue()) : false;
    }

    public boolean equalsTo(final Integer id){
        return id != null ? (id.intValue() == this.id.intValue()) : false;
    }

    public static ClassEnum of(final StatusEntity status) {
        return valueOf(status.getName());
    }

    public static ClassEnum getStatusEnum(final String statusLabel) {
        ClassEnum statusEnum = null;
        statusEnum = valueOf(statusLabel);
        return statusEnum;
    }

    public static ClassEnum valueOf(Integer id) {
        if (id!=null) {
            for (ClassEnum item : values()) {
                if (item.id.equals(id)) {
                    return item;
                }
            }
        }
        throw new IllegalArgumentException("Client Status Id invalid : [" + id + "]");
    }


}

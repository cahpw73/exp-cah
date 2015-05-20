package ch.swissbytes.domain.types;

import ch.swissbytes.domain.model.entities.RoleEntity;

import java.io.Serializable;

/**
 * Created by christian on 15/09/14.
 */
public enum RoleEnum implements Serializable {

    /**/SENIOR(10,"Senior"),
    /**/JUNIOR(11,"Junior"),
    /**/VISITOR(12,"Visitor"),
    /**/ADMINISTRATOR(13,"Administrator"),
    /**/FULL_WITH_COMMIT(14,"Full with Commit"),
    /**/FULL(15,"Full"),
    /**/RESTRICTED(16,"Restricted"),
    /**/READ_ONLY(17,"Read Only");

    private final String label;
    private final Integer id;

    RoleEnum(Integer id, String label){
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public boolean equalsTo(final RoleEnum roleEnum){
        return roleEnum != null ? (roleEnum.getId().intValue() == this.id.intValue()) : false;
    }

    public boolean equalsTo(final Integer id){
        return id != null ? (id.intValue() == this.id.intValue()) : false;
    }

    public static RoleEnum of(final RoleEntity roleEntity) {
        return valueOf(roleEntity.getName());
    }

    public static RoleEnum getRoleEnum(final String roleLabel) {
        RoleEnum roleEnum = null;
        roleEnum = valueOf(roleLabel);
        return roleEnum;
    }

    public static RoleEnum valueOf(Integer id) {
        if (id!=null) {
            for (RoleEnum item : values()) {
                if (item.id.equals(id)) {
                    return item;
                }
            }
        }
        throw new IllegalArgumentException("User Role Id invalid : [" + id + "]");
    }
}

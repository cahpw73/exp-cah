package ch.swissbytes.fqmes.util;

import java.util.EnumSet;


import ch.swissbytes.domain.repository.entities.OptionsEntity;
import org.apache.commons.lang3.ObjectUtils;


import javax.persistence.EntityManager;


public enum OptionsEnum {

    /**/USER_LIST(1),
    /**/USER_CREATE(2),
    /**/USER_UPDATE(3),
    /**/USER_VIEW(4),
    /**/USER_DELETE(5),
    /* PURCHASE_ORDER*/
    /**/PURCHASE_ORDER_LIST(100),
    /**/PURCHASE_ORDER_CREATE(101),
    /**/PURCHASE_ORDER_UPDATE(102),
    /**/PURCHASE_ORDER_VIEW(103),
    /**/PURCHASE_ORDER_DELETE(104),
    /**/PURCHASE_ORDER_SPLIT(105),
    /**REPORTS */
    /**/REPORT_LIST(200),
    /**/REPORT_RECEIVABLE_MANIFEST(201),
    /**/REPORT_JOB_SUMMARY(202);

    private Integer id;

    public static final EnumSet<OptionsEnum> EMPTY_ENUMSET = EnumSet.noneOf(OptionsEnum.class);

    public static final EnumSet<OptionsEnum> DEFINED_ON_DB = EnumSet.of(USER_LIST,
            USER_CREATE,
            USER_UPDATE,
            USER_VIEW,
            USER_DELETE,
            PURCHASE_ORDER_LIST,
            PURCHASE_ORDER_CREATE,
            PURCHASE_ORDER_UPDATE,
            PURCHASE_ORDER_VIEW,
            PURCHASE_ORDER_DELETE,
            PURCHASE_ORDER_SPLIT,
            REPORT_LIST,
            REPORT_RECEIVABLE_MANIFEST,
            REPORT_JOB_SUMMARY);

    private OptionsEnum(Integer id) {
        this.id = id;
    }

    public static OptionsEnum of(final OptionsEntity op) {
        return valueOf(op.getName());
    }


    public boolean equalsTo(OptionsEntity option) {
        return option != null && ObjectUtils.equals(this.id, option.getId());
    }


    public OptionsEntity getEntity(EntityManager persister) {
        OptionsEntity option = null;
        if (persister != null) {
            option = persister.find(OptionsEntity.class, id);
        }
        return option;
    }


    public Integer getId() {
        return id;
    }

    public static OptionsEnum getOptionsEnum(Integer optionId) {
        if (optionId != null) {
            for (OptionsEnum item : values()) {
                if (item.getId().equals(optionId)) {
                    return item;
                }
            }
        }
        throw new IllegalArgumentException("Value invalid id [" + optionId + "]");
    }

}

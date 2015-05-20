package ch.swissbytes.domain.types;

import ch.swissbytes.domain.model.entities.StatusEntity;

import java.io.Serializable;

public enum ModuleSystemEnum implements FqmEnum<ModuleSystemEnum>, Serializable  {

    EXPEDITING, PROCUREMENT;


    public boolean equalsTo(final ModuleSystemEnum stEnum){
        return stEnum != null ? (stEnum.ordinal() == this.ordinal()) : false;
    }

    public boolean equalsTo(final Integer id){
        return id != null ? (id.intValue() == this.ordinal()) : false;
    }

}

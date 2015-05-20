package ch.swissbytes.domain.model.entities;


import ch.swissbytes.domain.types.ModuleSystemEnum;

import javax.persistence.*;

@Entity
@Table(name="MODULE_ENTITY"
    ,schema="public"
)
public class ModuleEntity implements java.io.Serializable {

    private Integer id;
    private String name;
    private ModuleSystemEnum moduleSystem;

    public ModuleEntity() {
    }

    public ModuleEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id

    @Column(name="id", nullable=false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="name", nullable=false, length=50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column (name = "module_system",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public ModuleSystemEnum getModuleSystem() {
        return moduleSystem;
    }

    public void setModuleSystem(ModuleSystemEnum moduleSystem) {
        this.moduleSystem = moduleSystem;
    }
}



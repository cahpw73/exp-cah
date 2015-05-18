package ch.swissbytes.domain.model.entities;

import ch.swissbytes.domain.types.ModuleSystemEnum;

import javax.persistence.*;

/**
 * Created by christian on 12/09/14.
 */
@Entity
@Table(name = "role")
public class RoleEntity {

    private Integer id;
    private String name;
    private ModuleSystemEnum moduleSystem;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="name", length = 50, nullable = false)
    public String getName() {
        return name;
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

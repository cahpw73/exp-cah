package ch.swissbytes.domain.model.entities;

import ch.swissbytes.domain.types.ModuleSystemEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by christian on 12/09/14.
 */
@Entity
@Table(name = "module_granted_access")
public class ModuleGrantedAccessEntity {

    private Integer id;
    private Boolean moduleAccess;
    private ModuleSystemEnum moduleSystem;
    private UserEntity userEntity;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "MODULE_GRANTED_ACCESS_ID_SEQ", allocationSize = 1, initialValue = 100)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="module_access",nullable = true)
    public Boolean getModuleAccess() {
        return moduleAccess;
    }

    public void setModuleAccess(Boolean moduleAccess) {
        this.moduleAccess = moduleAccess;
    }

    @Column (name = "module_system",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public ModuleSystemEnum getModuleSystem() {
        return moduleSystem;
    }

    public void setModuleSystem(ModuleSystemEnum moduleSystem) {
        this.moduleSystem = moduleSystem;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}

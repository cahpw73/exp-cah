package ch.swissbytes.domain.model.entities;

import ch.swissbytes.domain.types.ModuleSystemEnum;

import javax.persistence.*;

/**
 * Created by christian on 12/09/14.
 */
@Entity
@Table(name = "user_role")
public class UserRoleEntity {

    private Integer id;
    private ModuleSystemEnum moduleSystem;
    private UserEntity user;
    private RoleEntity role;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "USER_ROLE_ID_SEQ", allocationSize = 1, initialValue = 10)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="role_id")
    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }
}

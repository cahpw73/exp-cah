package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.types.StatusEnum;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class UserCriteria implements Serializable {

    private String username;
    private String name;
    private String email;
    private StatusEnum status;

    public UserCriteria() {

    }

    public UserCriteria(String username, String name, String email, StatusEnum status) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public void clear() {
        setUsername(null);
        setName(null);
        setEmail(null);
        setStatus(null);
    }
}

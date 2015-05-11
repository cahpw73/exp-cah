package ch.swissbytes.domain.repository.entities;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by alvaro on 9/2/14.
 */

@Named
@Entity
@Table(name = "status")
public class StatusEntity implements Serializable{

    private Integer id;
    private String name;


    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @Basic
    @Column(name = "name", unique = true, nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

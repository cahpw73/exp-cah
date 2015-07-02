package ch.swissbytes.domain.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="options"
        ,schema="public"
)
public class OptionsEntity implements Serializable {


     private Integer id;
     private String name;
     private ModuleEntity module;
     private String url;

    public OptionsEntity() {
    }

    public OptionsEntity(Integer id, String name, ModuleEntity modulo) {
       this.id = id;
       this.name = name;
       this.module = modulo;
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
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="module_id", nullable=false)
    public ModuleEntity getModule() {
        return this.module;
    }
    
    public void setModule(ModuleEntity module) {
        this.module = module;
    }

    @Column(name="url", nullable=false, length=500)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

// The following is extra code specified in the hbm.xml files

		    private static final long serialVersionUID = 1L;
		
  // end of extra code specified in the hbm.xml files

}



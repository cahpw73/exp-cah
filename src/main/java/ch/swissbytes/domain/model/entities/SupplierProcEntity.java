package ch.swissbytes.domain.model.entities;

/**
 * Created by alvaro on 9/8/14.
 */

import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@Entity
@Table(name = "supplier_proc")
public class SupplierProcEntity implements Serializable {

    private Long id;
    private StatusEnum status;
    private String supplierId;
    private String company;
    private String street;
    private String suburb;
    private String state;
    private String postCode;
    private String country;
    private String phone;
    private String fax;
    private String companyEmail;
    private String abnRegNo;
    private String comments;
    private Date lastUpdate;

    private List<ContactEntity> contacts = new ArrayList<>();
    private List<CategoryEntity> categories = new ArrayList<>();
    private List<BrandEntity> brands = new ArrayList<>();

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "SUPPLIER_PROC_ID_SEQ", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "LAST_UPDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Size(max = 1000)
    @Column(name = "DESCRIPTION", length = 1000)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Size(max = 250)
    @Column(name = "ABN_REG_NO", length = 250)
    public String getAbnRegNo() {
        return abnRegNo;
    }

    public void setAbnRegNo(String abnRegNo) {
        this.abnRegNo = abnRegNo;
    }

    @NotNull
    @Size(max = 250)
    @Column(name = "SUPPLIER_ID", length = 250)
    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    @Size(max = 250)
    @Column(name = "COMPANY", length = 250)
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Size(max = 250)
    @Column(name = "STREET", length = 250)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Size(max = 250)
    @Column(name = "SUBURB", length = 250)
    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    @Size(max = 250)
    @Column(name = "STATE", length = 250)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Size(max = 250)
    @Column(name = "POST_CODE", length = 250)
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Size(max = 250)
    @Column(name = "COUNTRY", length = 250)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Size(max = 250)
    @Column(name = "PHONE", length = 250)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Size(max = 250)
    @Column(name = "FAX", length = 250)
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Size(max = 250)
    @Column(name = "COMPANY_EMAIL", length = 250)
    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    @Transient
    public List<ContactEntity> getContacts() {
        return contacts;
    }

    @Transient
    public List<CategoryEntity> getCategories() {
        return categories;
    }

    @Transient
    public List<BrandEntity> getBrands() {
        return brands;
    }


}

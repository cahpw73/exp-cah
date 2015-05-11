package ch.swissbytes.domain.repository.entities;


//import org.hibernate.envers.Audited;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Named
@Entity
@Table(name = "verification_token")
//@Audited
public class VerificationTokenEntity implements Serializable{

    private Long id;
    private String token;
    private boolean verified;
    private UserEntity user;
    private Date expirationDate;

    public VerificationTokenEntity() {

    }

    /*public VerificationTokenEntity(UserEntity user) {

        this.user = user;
    }*/

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "verification_token_id_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "token", unique = true, nullable = false)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @Column(name = "verified", nullable = false)
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Basic
    @Column(name = "expiration_date", nullable = false)
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    private Date calculateExpirationDate(int expirationTimeInHours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, expirationTimeInHours);
        return cal.getTime();
    }

    public boolean hasExpired() {
        Date tokenDate = new Date();
        return tokenDate.after(expirationDate);
    }

    public String toStringReportValues() {
        return String.format("token=%s verified=%s user=%d expiration date=%s", token, verified, user.getId(), expirationDate);
    }
}
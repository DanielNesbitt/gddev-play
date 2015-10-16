package models;

import javax.persistence.*;

/**
 * @author Daniel Nesbitt
 */
@Entity
@Table(name = "USERS")
public class User {

    // ------------- Variables -------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String forename;

    @Column(nullable = false)
    private String surname;

    // ------------- Public - Getters -------------

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    // ------------- Public - Setters -------------

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}

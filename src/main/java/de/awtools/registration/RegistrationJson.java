package de.awtools.registration;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Holds the registration data.
 *
 * @author Andre Winkler
 */
public class RegistrationJson implements Serializable {

    private static final long serialVersionUID = 7765899606128897504L;

    @NotNull
    private String nickname;

    @NotNull
    private String name;

    @NotNull
    private String firstname;

    @NotNull
    private String password;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String applicationName;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}

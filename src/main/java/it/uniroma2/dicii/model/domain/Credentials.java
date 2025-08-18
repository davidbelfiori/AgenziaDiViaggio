package it.uniroma2.dicii.model.domain;

public class Credentials {

    private final String email;
    private final String password;
    private final Role role;

    public Credentials(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }


    public Role getRole() {
        return role;
    }
}

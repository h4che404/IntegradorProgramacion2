package integrado.prog2.entities;

import integrado.prog2.enums.Rol;

import java.time.LocalDateTime;
import java.util.Objects;

public class Usuario extends BaseEntity {
    private String firstName;
    private String lastName;
    private String mail;
    private String phone;
    private String password;
    private Rol role;

    public Usuario() {
        super();
    }

    public Usuario(String firstName, String lastName, String mail, String phone, String password, Rol role) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public Usuario(Long id, boolean eliminado, LocalDateTime createdAt, String firstName, String lastName, String mail,
                   String phone, String password, Rol role) {
        super(id, eliminado, createdAt);
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRole() {
        return role;
    }

    public void setRole(Rol role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s | %s | %s",
                getId(),
                firstName,
                lastName,
                mail,
                role != null ? role.getLabel() : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario user)) {
            return false;
        }
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

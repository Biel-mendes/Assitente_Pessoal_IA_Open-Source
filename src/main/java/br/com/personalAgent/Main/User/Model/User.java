package br.com.personalAgent.Main.User.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USUARIO")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USU_INT_ID", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "USU_STR_NAME", nullable = false)
    private String name;

    @NotBlank
    @Email
    @Column(name = "USU_STR_EMAIL", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "USU_STR_PASSWORD", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "USU_STR_TYPE", nullable = false)
    private UserType type;

    @Column(name = "USU_DATE_CREATE", updatable = false)
    private LocalDateTime create;

    protected User() {
    }

    public User(UserType type, String password, String name, String email) {
        this.type = type;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    @PrePersist
    protected void onCreate() {
        this.create = LocalDateTime.now();
    }

    // Getters e Setters
    public UUID getId() {
        return id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public LocalDateTime getCreate() {
        return create;
    }

}
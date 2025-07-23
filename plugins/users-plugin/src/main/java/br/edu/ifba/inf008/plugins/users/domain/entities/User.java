package br.edu.ifba.inf008.plugins.users.domain.entities;

import br.edu.ifba.inf008.core.ui.annotations.TableIgnore;
import br.edu.ifba.inf008.core.ui.annotations.TableLabel;
import br.edu.ifba.inf008.core.ui.annotations.TableColumnSize;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    @TableLabel("#")
    @TableColumnSize(50)
    private Long userId;

    @Column(nullable = false)
    @TableColumnSize(250)
    private String name;

    @Column(nullable = false, unique = true)
    @TableLabel("E-mail")
    @TableColumnSize(250)
    private String email;

    @Column(name = "registered_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @TableIgnore
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "deactivated_at")
    @TableIgnore
    private LocalDateTime deactivatedAt;

    public User() {
    }

    public User(Long userId, String name, String email, LocalDateTime registeredAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.registeredAt = registeredAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", registeredAt=" + registeredAt +
                ", deactivatedAt=" + deactivatedAt +
                '}';
    }

}

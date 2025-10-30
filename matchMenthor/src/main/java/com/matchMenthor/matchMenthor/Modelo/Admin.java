package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.Duration;


@Entity
@Table(name = "admins",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private boolean bloqueadoAdmin = false;

    @Column(nullable = false)
    private int intentosFallidosAdmin = 0;

    private LocalDateTime fechaBloqueo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Ejemplo: si quieres registrar qué admin hizo cambios
    @Column(name = "last_action")
    private String lastAction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isBloqueadoAdmin() {
        return bloqueadoAdmin;
    }

    public void setBloqueadoAdmin(boolean bloqueadoAdmin) {
        this.bloqueadoAdmin = bloqueadoAdmin;
    }

    public int getIntentosFallidosAdmin() {
        return intentosFallidosAdmin;
    }

    public void setIntentosFallidosAdmin(int intentosFallidosAdmin) {
        this.intentosFallidosAdmin = intentosFallidosAdmin;
    }

    public LocalDateTime getFechaBloqueo() {
        return fechaBloqueo;
    }

    public void setFechaBloqueo(LocalDateTime fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }
}


package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Duration;


@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Relaciones ===

    // Notas del estudiante (lado inverso)
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentGrades> grades = new ArrayList<>();

    // Perfil del mentor 1:1 (lado inverso)
    // Si usaste la Opción A (shared PK) en MentorProfile:
    @OneToOne(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MentorProfiles mentorProfile;

    // Matches donde soy estudiante
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matches> matchesAsStudent = new ArrayList<>();

    // Matches donde soy mentor
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matches> matchesAsMentor = new ArrayList<>();

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public LocalDateTime getFechaBloqueo() {
        return fechaBloqueo;
    }

    public void setFechaBloqueo(LocalDateTime fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }

    // === Campos ===

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role; // STUDENT o MENTOR

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false)
    private boolean bloqueado = false;

    @Column(nullable = false)
    private int intentosFallidos = 0;

    private LocalDateTime fechaBloqueo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === Enum de rol ===
    public enum Role {
        STUDENT, MENTOR
    }
}


package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // Info adicional del estudiante (1:1 inverso)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private StudentInfo studentInfo;

    // Info adicional del mentor (1:1 inverso)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MentorInfo mentorInfo;

    // === getters & setters nuevos ===
    public StudentInfo getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
        if (studentInfo != null) {
            studentInfo.setUser(this);
        }
    }

    public MentorInfo getMentorInfo() {
        return mentorInfo;
    }

    public void setMentorInfo(MentorInfo mentorInfo) {
        this.mentorInfo = mentorInfo;
        if (mentorInfo != null) {
            mentorInfo.setUser(this);
        }
    }

}


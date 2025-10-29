package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "student_info")
public class StudentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // programa académico del estudiante (por ejemplo "Ingeniería de Software")
    @Column(nullable = false, length = 120)
    private String program;

    // semestre actual (por ejemplo 3, 5, 8...)
    @Column(nullable = false)
    private Integer semester;

    // Relación 1:1 con Users
    // unique = true => un usuario no puede tener dos filas de StudentInfo
    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_studentinfo_user")
    )
    private Users user;

    // ====== Getters y Setters ======

    public Long getId() {
        return id;
    }

    public String getPrograma() {
        return program;
    }

    public void setPrograma(String programa) {
        this.program = programa;
    }

    public Integer getSemestre() {
        return semester;
    }

    public void setSemestre(Integer semestre) {
        this.semester = semestre;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}

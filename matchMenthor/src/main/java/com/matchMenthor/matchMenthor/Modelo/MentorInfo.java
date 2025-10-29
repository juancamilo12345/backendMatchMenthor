package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "mentor_info")
public class MentorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // disponibilidad del mentor (ej: "Lunes-Viernes 18:00-21:00")
    @Column(nullable = false, length = 150)
    private String availability;

    // habilidades técnicas/blandas del mentor (ej: "React, Spring Boot, liderazgo, CV coaching")
    @Column(nullable = false, length = 200)
    private String skills;

    // biografía / descripción personal del mentor
    @Column(length = 500)
    private String biographie;

    // Relación 1:1 con Users
    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_mentorinfo_user")
    )
    private Users user;

    // ====== Getters y Setters ======

    public Long getId() {
        return id;
    }

    public String getDisponibilidad() {
        return availability;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.availability = disponibilidad;
    }

    public String getHabilidades() {
        return skills;
    }

    public void setHabilidades(String habilidades) {
        this.skills = habilidades;
    }

    public String getBiografia() {
        return biographie;
    }

    public void setBiografia(String biografia) {
        this.biographie = biografia;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}

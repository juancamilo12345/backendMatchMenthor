package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_profiles")
public class MentorProfiles {

    // PK que además será FK hacia users.id
    @Id
    @Column(name = "mentor_id")
    private Long mentorId;

    // Relación 1:1 con Users compartiendo el ID
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId                             // <-- comparte el PK con la FK
    @JoinColumn(name = "mentor_id", referencedColumnName = "id", nullable = false, unique = true)
    private Users mentor;

    @Column(name = "biography", nullable = false, length = 400)
    private String biography;

    @Column(name = "skills", nullable = false, length = 400)
    private String skills;

    @Column(name = "availability", nullable = false, length = 50)
    private String availability;

    @Column(name = "rating")
    private Double rating;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false) // no uses "DataTime"
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setMentor(Users mentorUser) {
        this.mentor = mentorUser;
    }

    public Users getMentor() {
        return mentor;
    }
}

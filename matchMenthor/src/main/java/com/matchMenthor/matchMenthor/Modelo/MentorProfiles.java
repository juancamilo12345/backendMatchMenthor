package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_profiles")
@Getter @Setter
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
}

package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "matches",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "mentor_id"})
)
public class Matches { // <- mejor nombre en singular

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK autoincrement

    // FK -> users(id) (estudiante)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    // FK -> users(id) (mentor)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Users mentor;

    @Column(nullable = false)
    private Double score; // 0-100

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }
}


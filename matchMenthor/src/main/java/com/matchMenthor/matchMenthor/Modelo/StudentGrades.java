package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_grades",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "subject_id"}) // opcional, evita duplicados por estudiante/materia
        })
public class StudentGrades {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // mejor Long y autoincrement

    // FK hacia Users -> student_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    // FK hacia Subjects -> subject_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subjects subject;

    @Column(nullable = false)
    private Double grade;

    @Column(name = "taken_at", nullable = false)
    private LocalDateTime takenAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(LocalDateTime takenAt) {
        this.takenAt = takenAt;
    }
}

package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "mentoring_sessions")
public class MentoringSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mentor dueño de la sesión
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Users mentor;

    // Estudiante atendido en la sesión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Users student;

    // si quieres ligarla al match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Matches match;

    // fecha y hora
    @Column(name = "session_date")
    private LocalDate sessionDate;

    @Column(name = "session_time")
    private LocalTime sessionTime;

    // AGENDADA | COMPLETADA | CANCELADA
    @Column(length = 20, nullable = false)
    private String status = "AGENDADA";

    // el estudiante confirmó?
    @Column(name = "student_confirmed")
    private Boolean studentConfirmed = Boolean.FALSE;

    // tema planeado
    @Column(name = "planned_topic", length = 300)
    private String plannedTopic;

    // resumen de lo visto (lo llena el mentor al completar)
    @Column(columnDefinition = "TEXT")
    private String sessionSummary;

    // observaciones internas del mentor
    @Column(columnDefinition = "TEXT")
    private String mentorNotes;

    // calificación que el estudiante le dio al mentor
    @Column(name = "student_rating_to_mentor")
    private Integer studentRatingToMentor;

    // calificación que el mentor le dio al estudiante
    @Column(name = "mentor_rating_to_student")
    private Integer mentorRatingToStudent;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== getters & setters =====

    public Long getId() {
        return id;
    }

    public Users getMentor() {
        return mentor;
    }

    public void setMentor(Users mentor) {
        this.mentor = mentor;
    }

    public Users getStudent() {
        return student;
    }

    public void setStudent(Users student) {
        this.student = student;
    }

    public Matches getMatch() {
        return match;
    }

    public void setMatch(Matches match) {
        this.match = match;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(LocalTime sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getStudentConfirmed() {
        return studentConfirmed;
    }

    public void setStudentConfirmed(Boolean studentConfirmed) {
        this.studentConfirmed = studentConfirmed;
    }

    public String getPlannedTopic() {
        return plannedTopic;
    }

    public void setPlannedTopic(String plannedTopic) {
        this.plannedTopic = plannedTopic;
    }

    public String getSessionSummary() {
        return sessionSummary;
    }

    public void setSessionSummary(String sessionSummary) {
        this.sessionSummary = sessionSummary;
    }

    public String getMentorNotes() {
        return mentorNotes;
    }

    public void setMentorNotes(String mentorNotes) {
        this.mentorNotes = mentorNotes;
    }

    public Integer getStudentRatingToMentor() {
        return studentRatingToMentor;
    }

    public void setStudentRatingToMentor(Integer studentRatingToMentor) {
        this.studentRatingToMentor = studentRatingToMentor;
    }

    public Integer getMentorRatingToStudent() {
        return mentorRatingToStudent;
    }

    public void setMentorRatingToStudent(Integer mentorRatingToStudent) {
        this.mentorRatingToStudent = mentorRatingToStudent;
    }
}

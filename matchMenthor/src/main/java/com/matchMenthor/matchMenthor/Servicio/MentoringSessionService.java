package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Matches;
import com.matchMenthor.matchMenthor.Modelo.MentoringSession;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.MatchRepository;
import com.matchMenthor.matchMenthor.Repositorio.MentoringSessionRepository;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MentoringSessionService {

    private final MentoringSessionRepository repository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public MentoringSessionService(MentoringSessionRepository repository,
                                   UserRepository userRepository,
                                   MatchRepository matchRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    public List<MentoringSession> getForMentor(Long mentorId) {
        return repository.findByMentor_IdOrderBySessionDateDesc(mentorId);
    }

    public MentoringSession createForMentor(Long mentorId,
                                            Long studentId,
                                            Long matchId,
                                            String date,
                                            String time,
                                            String plannedTopic) {

        Users mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor no encontrado"));

        MentoringSession s = new MentoringSession();
        s.setMentor(mentor);
        s.setPlannedTopic(plannedTopic);
        s.setStatus("AGENDADA");

        // fecha/hora opcionales
        if (date != null && !date.isBlank()) {
            s.setSessionDate(LocalDate.parse(date)); // formato yyyy-MM-dd
        }
        if (time != null && !time.isBlank()) {
            s.setSessionTime(LocalTime.parse(time)); // formato HH:mm
        }

        // si viene el estudiante
        if (studentId != null) {
            Users student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
            s.setStudent(student);
        }

        // si viene el match
        if (matchId != null) {
            Matches m = matchRepository.findById(matchId)
                    .orElseThrow(() -> new RuntimeException("Match no encontrado"));
            s.setMatch(m);
        }

        return repository.save(s);
    }

    public MentoringSession updateFeedback(Long mentorId,
                                           Long sessionId,
                                           String notes,
                                           Integer mentorRating,
                                           String summary,
                                           String status) {

        MentoringSession session = repository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // seguridad mínima: la sesión debe ser del mentor que está logueado
        if (!session.getMentor().getId().equals(mentorId)) {
            throw new RuntimeException("No puedes editar una sesión de otro mentor");
        }

        if (notes != null) session.setMentorNotes(notes);
        if (mentorRating != null) session.setMentorRatingToStudent(mentorRating);
        if (summary != null) session.setSessionSummary(summary);
        if (status != null) session.setStatus(status);

        return repository.save(session);
    }
}

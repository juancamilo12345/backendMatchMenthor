package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.MentoringSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

    // listar solo las sesiones del mentor
    List<MentoringSession> findByMentor_IdOrderBySessionDateDesc(Long mentorId);
}

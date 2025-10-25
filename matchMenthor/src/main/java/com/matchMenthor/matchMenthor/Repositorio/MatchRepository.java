package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.Matches;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MatchRepository extends JpaRepository<Matches, Long> {

    // Top-N mentores para un estudiante por score
    List<Matches> findTop10ByStudent_IdOrderByScoreDesc(Long studentId);

    // ¿Existe ya un match entre estudiante y mentor?
    Optional<Matches> findByStudent_IdAndMentor_Id(Long studentId, Long mentorId);

    // Cambios de estado
    long countByStudent_IdAndStatus(Long studentId, Matches.Status status);

    // Listar matches por mentor (para su panel)
    List<Matches> findByMentor_IdOrderByCreatedAtDesc(Long mentorId);
}

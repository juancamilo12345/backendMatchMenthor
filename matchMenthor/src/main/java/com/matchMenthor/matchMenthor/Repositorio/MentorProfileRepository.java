package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.MentorProfiles;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfiles, Long> {

    // Opción A (shared PK): el ID del perfil == ID del mentor (users.id)
    Optional<MentorProfiles> findByMentor_Id(Long mentorId);

    // Búsquedas básicas por texto (skills, bio). Ajusta a tu motor real (ILIKE en Postgres).
    @Query("""
           SELECT mp FROM MentorProfiles mp
           WHERE LOWER(mp.skills) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(mp.biography) LIKE LOWER(CONCAT('%', :q, '%'))
           """)
    java.util.List<MentorProfiles> searchByText(String q);
}

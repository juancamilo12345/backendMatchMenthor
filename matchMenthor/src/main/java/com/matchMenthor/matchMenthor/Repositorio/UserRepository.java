package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.MentorProfiles;
import com.matchMenthor.matchMenthor.Modelo.Users;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByEmail (String email);
    boolean existsByEmail (String email);

    // Ej: listar mentores o estudiantes
    List<Users> findByRole(Users.Role role);

    // Cargar mentorProfile junto con el usuario (evita N+1 al leer)
    @EntityGraph(attributePaths = {"MentorProfiles"})
    Optional<Users> findWithMentorProfileById(Long id);

    // Conteos rápidos
    long countByRole(Users.Role role);
}

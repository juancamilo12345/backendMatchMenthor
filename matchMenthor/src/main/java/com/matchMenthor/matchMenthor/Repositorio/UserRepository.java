package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    // listar por rol (STUDENT, MENTOR, ADMIN)
    List<Users> findByRole(Users.Role role);

    // Cargar mentorProfile junto con el usuario (evita N+1 al leer)
    @EntityGraph(attributePaths = {"mentorProfile"})
    Optional<Users> findWithMentorProfileById(Long id);

    // Conteos rápidos por rol
    long countByRole(Users.Role role);
}


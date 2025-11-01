package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subjects, Long> {

    // Buscar una materia por su código
    Optional<Subjects> findByCode(String code);

    // Verificar si ya existe una materia con ese código
    boolean existsByCode(String code);

    // Buscar una materia por su nombre (opcional, útil si cambias nombre)
    Optional<Subjects> findBySubjectName(String subjectName);
}




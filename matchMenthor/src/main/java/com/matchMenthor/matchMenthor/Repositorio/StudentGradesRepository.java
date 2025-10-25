package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.StudentGrades;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentGradesRepository extends JpaRepository<StudentGrades, Long> {
    // Notas de un estudiante (más recientes primero, versión paginada)
    Page<StudentGrades> findByStudent_IdOrderByTakenAtDesc(Long studentId, Pageable pageable);

    // Notas de un estudiante por materia
    List<StudentGrades> findByStudent_IdAndSubject_IdOrderByTakenAtDesc(Long studentId, Long subjectId);

    // Búsqueda combinada (por código de materia)
    List<StudentGrades> findByStudent_IdAndSubject_Code(Long studentId, String subjectCode);

    // --- Agregados para el panel del estudiante ---

    interface StudentStatsView {
        Double getAvgGrade();
        Long getPassed();
        Long getFailed();
    }

    @Query("""
           SELECT 
             AVG(g.grade)      AS avgGrade,
             SUM(CASE WHEN g.grade >= 3.0 THEN 1 ELSE 0 END) AS passed,
             SUM(CASE WHEN g.grade  < 3.0 THEN 1 ELSE 0 END) AS failed
           FROM StudentGrades g
           WHERE g.student.id = :studentId
           """)
    StudentStatsView computeStats(@Param("studentId") Long studentId);
}

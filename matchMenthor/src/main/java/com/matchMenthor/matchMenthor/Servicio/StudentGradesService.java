package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.StudentGrades;
import com.matchMenthor.matchMenthor.Repositorio.StudentGradesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentGradesService {

    private final StudentGradesRepository repository;

    public StudentGradesService(StudentGradesRepository repository) {
        this.repository = repository;
    }

    // =====================================================
    // CRUD GENERAL
    // =====================================================

    public List<StudentGrades> getAll() {
        return repository.findAll();
    }

    public Optional<StudentGrades> getById(Long id) {
        return repository.findById(id);
    }

    public StudentGrades create(StudentGrades grade) {
        return repository.save(grade);
    }

    /**
     * Actualiza una nota existente. Solo toca los campos que tienen sentido
     * para una edición de nota: grade y takenAt.
     */
    public StudentGrades update(Long id, StudentGrades newGrade) {
        return repository.findById(id)
                .map(grade -> {
                    grade.setGrade(newGrade.getGrade());
                    grade.setTakenAt(newGrade.getTakenAt());
                    // NO tocamos student ni subject aquí
                    return repository.save(grade);
                })
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // =====================================================
    // LO QUE USA EL FRONT DEL ESTUDIANTE
    // =====================================================

    /**
     * Trae las notas de un estudiante, ordenadas de la más reciente a la más vieja.
     * Versión paginada por si el front luego quiere lazy loading.
     */
    public Page<StudentGrades> getByStudentPaged(Long studentId, int page, int size) {
        return repository.findByStudent_IdOrderByTakenAtDesc(
                studentId,
                PageRequest.of(page, size)
        );
    }

    /**
     * Trae TODAS las notas de un estudiante (sin paginar).
     * Úsalo si tu front solo va a mostrar la lista tal cual.
     */
    public List<StudentGrades> getByStudentId(Long studentId) {
        // aquí le pongo un page de 1000 para no traernos toda la tabla
        return repository
                .findByStudent_IdOrderByTakenAtDesc(studentId, PageRequest.of(0, 1000))
                .getContent();
    }

    /**
     * Notas de un estudiante para una materia específica (por id de subject).
     */
    public List<StudentGrades> getByStudentAndSubject(Long studentId, Long subjectId) {
        return repository.findByStudent_IdAndSubject_IdOrderByTakenAtDesc(studentId, subjectId);
    }

    /**
     * Notas de un estudiante para una materia específica (por código de subject).
     * Esto cuadra con el front que manda "codigo".
     */
    public List<StudentGrades> getByStudentAndSubjectCode(Long studentId, String subjectCode) {
        return repository.findByStudent_IdAndSubject_Code(studentId, subjectCode);
    }

    /**
     * Estadísticas (promedio, aprobadas, reprobadas).
     */
    public StudentGradesRepository.StudentStatsView getStats(Long studentId) {
        return repository.computeStats(studentId);
    }

    // =====================================================
    // HELPER OPCIONAL (por si el front solo manda la nota)
    // =====================================================

    public StudentGrades updateOnlyGrade(Long id, Double newGrade) {
        return repository.findById(id)
                .map(grade -> {
                    grade.setGrade(newGrade);
                    grade.setTakenAt(LocalDateTime.now());
                    return repository.save(grade);
                })
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));
    }
}

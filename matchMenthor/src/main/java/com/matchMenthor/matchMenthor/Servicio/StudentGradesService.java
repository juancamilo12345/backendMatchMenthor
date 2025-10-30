package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.StudentGrades;
import com.matchMenthor.matchMenthor.Repositorio.StudentGradesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentGradesService {

    private final StudentGradesRepository repository;

    public StudentGradesService(StudentGradesRepository repository) {
        this.repository = repository;
    }

    // ===== CRUD general (admin o pruebas) =====

    public List<StudentGrades> getAll() {
        return repository.findAll();
    }

    public Optional<StudentGrades> getById(Long id) {
        return repository.findById(id);
    }

    public StudentGrades create(StudentGrades grade) {
        return repository.save(grade);
    }

    public StudentGrades update(Long id, StudentGrades newGrade) {
        return repository.findById(id)
                .map(grade -> {
                    // solo actualizamos lo que tiene sentido
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

    // ===== Lo que necesitas para el front del estudiante =====

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
        // usamos el método por código de materia si quieres filtrar
        // pero aquí usamos el paginado con un size grande, o simplemente:
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
     * Esto cuadra MUY BIEN con tu front, que manda "codigo".
     */
    public List<StudentGrades> getByStudentAndSubjectCode(Long studentId, String subjectCode) {
        return repository.findByStudent_IdAndSubject_Code(studentId, subjectCode);
    }

    /**
     * Estadísticas de un estudiante (promedio, aprobadas, reprobadas).
     * Tu repo ya lo trae.
     */
    public StudentGradesRepository.StudentStatsView getStats(Long studentId) {
        return repository.computeStats(studentId);
    }
}

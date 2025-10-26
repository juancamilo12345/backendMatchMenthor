package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.StudentGrades;
import com.matchMenthor.matchMenthor.Repositorio.StudentGradesRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentGradesService {

    private final StudentGradesRepository repository;

    public StudentGradesService(StudentGradesRepository repository) {
        this.repository = repository;
    }

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
                    grade.setGrade(newGrade.getGrade());
                    grade.setTakenAt(newGrade.getTakenAt());
                    return repository.save(grade);
                })
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

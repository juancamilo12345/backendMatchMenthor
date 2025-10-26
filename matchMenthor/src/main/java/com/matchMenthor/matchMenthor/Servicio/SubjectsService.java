package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Subjects;
import com.matchMenthor.matchMenthor.Repositorio.SubjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectsService {

    private final SubjectRepository repository;

    public SubjectsService(SubjectRepository repository) {
        this.repository = repository;
    }

    public List<Subjects> getAll() {
        return repository.findAll();
    }

    public Optional<Subjects> getById(Long id) {
        return repository.findById(id);
    }

    public Subjects create(Subjects subject) {
        return repository.save(subject);
    }

    public Subjects update(Long id, Subjects newSubject) {
        return repository.findById(id)
                .map(subject -> {
                    subject.setCode(newSubject.getCode());
                    subject.setSubjectName(newSubject.getSubjectName());
                    return repository.save(subject);
                })
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}


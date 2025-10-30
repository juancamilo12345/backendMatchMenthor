package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Subjects;
import com.matchMenthor.matchMenthor.Repositorio.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubjectsService {

    private final SubjectRepository repository;

    public SubjectsService(SubjectRepository repository) {
        this.repository = repository;
    }

    public java.util.List<Subjects> getAll() {
        return repository.findAll();
    }

    public Optional<Subjects> getById(Long id) {
        return repository.findById(id);
    }

    public Subjects create(Subjects subject) {
        return repository.save(subject);
    }

    public Subjects update(Long id, Subjects subject) {
        return repository.findById(id)
                .map(s -> {
                    s.setSubjectName(subject.getSubjectName());
                    s.setCode(subject.getCode());
                    return repository.save(s);
                })
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ====== NUEVOS MÉTODOS ======

    public Optional<Subjects> findByCode(String code) {
        return repository.findByCode(code);
    }

    /**
     * Si no existe por código, la crea con el nombre que llega del front.
     */
    public Subjects findByCodeOrCreate(String code, String name) {
        return findByCode(code)
                .orElseGet(() -> {
                    Subjects s = new Subjects();
                    s.setCode(code);
                    s.setSubjectName(name);
                    return repository.save(s);
                });
    }
}


package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.StudentGrades;
import com.matchMenthor.matchMenthor.Servicio.StudentGradesService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/grades")
@CrossOrigin(origins = "*")
public class StudentGradesController {

    private final StudentGradesService service;

    public StudentGradesController(StudentGradesService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudentGrades> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public StudentGrades getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow();
    }

    @PostMapping
    public StudentGrades create(@RequestBody StudentGrades grade) {
        return service.create(grade);
    }

    @PutMapping("/{id}")
    public StudentGrades update(@PathVariable Long id, @RequestBody StudentGrades grade) {
        return service.update(id, grade);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}


package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.Subjects;
import com.matchMenthor.matchMenthor.Servicio.SubjectsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/subjects")
@CrossOrigin(origins = "*")
public class SubjectsController {

    private final SubjectsService service;

    public SubjectsController(SubjectsService service) {
        this.service = service;
    }

    @GetMapping
    public List<Subjects> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Subjects getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow();
    }

    @PostMapping
    public Subjects create(@RequestBody Subjects subject) {
        return service.create(subject);
    }

    @PutMapping("/{id}")
    public Subjects update(@PathVariable Long id, @RequestBody Subjects subject) {
        return service.update(id, subject);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

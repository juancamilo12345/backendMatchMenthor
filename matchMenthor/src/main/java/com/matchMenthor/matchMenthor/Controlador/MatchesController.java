package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.Matches;
import com.matchMenthor.matchMenthor.Servicio.MatchesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "*")
public class MatchesController {

    private final MatchesService service;

    public MatchesController(MatchesService service) {
        this.service = service;
    }

    @GetMapping
    public List<Matches> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Matches getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow();
    }

    @PostMapping
    public Matches create(@RequestBody Matches match) {
        return service.create(match);
    }

    @PutMapping("/{id}")
    public Matches update(@PathVariable Long id, @RequestBody Matches match) {
        return service.update(id, match);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}


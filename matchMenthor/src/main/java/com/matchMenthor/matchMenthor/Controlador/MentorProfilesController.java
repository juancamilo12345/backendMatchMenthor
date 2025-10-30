package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.MentorProfiles;
import com.matchMenthor.matchMenthor.Servicio.MentorProfilesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentorP")
@CrossOrigin(origins = "*")
public class MentorProfilesController {

    private final MentorProfilesService service;

    public MentorProfilesController(MentorProfilesService service) {
        this.service = service;
    }

    @GetMapping
    public List<MentorProfiles> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MentorProfiles getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow();
    }

    @PostMapping
    public MentorProfiles create(@RequestBody MentorProfiles profile) {
        return service.create(profile);
    }

    @PutMapping("/{id}")
    public MentorProfiles update(@PathVariable Long id, @RequestBody MentorProfiles profile) {
        return service.update(id, profile);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

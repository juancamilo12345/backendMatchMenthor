package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.MentorProfiles;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.MentorProfilesService;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mentors")
@CrossOrigin(origins = "*")
public class MentorController {

    private final MentorProfilesService mentorProfilesService;
    private final UsersService usersService;

    public MentorController(MentorProfilesService mentorProfilesService,
                            UsersService usersService) {
        this.mentorProfilesService = mentorProfilesService;
        this.usersService = usersService;
    }

    // =========================
    // GET /mentors/me/full-info
    // =========================
    @GetMapping("/me/full-info")
    public ResponseEntity<Map<String, Object>> getMyFullInfo(
            @RequestHeader("X-USER-ID") Long mentorId
    ) {
        Users mentorUser = usersService.getUserById(mentorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mentor (Users) no encontrado"
                ));

        // ✅ Crea automáticamente si no existe perfil
        MentorProfiles mentorProfile = mentorProfilesService
                .getOrCreateAndSaveIfNeeded(mentorUser);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", mentorUser.getId());
        resp.put("name", mentorUser.getName());
        resp.put("email", mentorUser.getEmail());
        resp.put("city", mentorUser.getCity());
        resp.put("role", mentorUser.getRole());
        resp.put("disponibilidad", mentorProfile.getAvailability() != null ? mentorProfile.getAvailability() : "");
        resp.put("habilidades", mentorProfile.getSkills() != null ? mentorProfile.getSkills() : "");
        resp.put("biografia", mentorProfile.getBiography() != null ? mentorProfile.getBiography() : "");
        resp.put("rating", mentorProfile.getRating() != null ? mentorProfile.getRating() : 0);

        return ResponseEntity.ok(resp);
    }

    // =========================
    // PUT /mentors/me/full-info
    // =========================
    @PutMapping("/me/full-info")
    public ResponseEntity<?> updateMyFullInfo(
            @RequestHeader("X-USER-ID") Long mentorId,
            @RequestBody Map<String, Object> body
    ) {
        Users mentorUser = usersService.getUserById(mentorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mentor (Users) no encontrado"
                ));

        MentorProfiles mentorProfile = mentorProfilesService
                .getOrCreateAndSaveIfNeeded(mentorUser);

        // ---- actualizar Users ----
        if (body.containsKey("name")) mentorUser.setName((String) body.get("name"));
        if (body.containsKey("email")) mentorUser.setEmail((String) body.get("email"));
        if (body.containsKey("city")) mentorUser.setCity((String) body.get("city"));
        usersService.createUser(mentorUser);

        // ---- actualizar MentorProfiles ----
        if (body.containsKey("disponibilidad"))
            mentorProfile.setAvailability((String) body.get("disponibilidad"));
        if (body.containsKey("habilidades"))
            mentorProfile.setSkills((String) body.get("habilidades"));
        if (body.containsKey("biografia"))
            mentorProfile.setBiography((String) body.get("biografia"));
        if (body.containsKey("rating")) {
            try {
                mentorProfile.setRating(Double.parseDouble(body.get("rating").toString()));
            } catch (Exception ignored) {}
        }

        mentorProfilesService.saveProfileForMentor(mentorUser, mentorProfile);

        return ResponseEntity.ok("Perfil de mentor actualizado correctamente");
    }

    // =========================
    // GET /mentors/search?q=...
    // =========================
    @GetMapping("/search")
    public ResponseEntity<?> searchMentors(
            @RequestParam(name = "q", required = false) String q
    ) {
        List<Users> mentorUsers = usersService.getAllMentors();
        List<MentorProfiles> profiles = mentorProfilesService.getAll();

        Map<Long, MentorProfiles> profileByMentorId = profiles.stream()
                .filter(p -> p.getMentor() != null && p.getMentor().getId() != null)
                .collect(Collectors.toMap(
                        p -> p.getMentor().getId(),
                        p -> p,
                        (a, b) -> a
                ));

        String qLower = (q != null) ? q.toLowerCase() : null;
        List<Map<String, Object>> resp = new ArrayList<>();

        for (Users m : mentorUsers) {
            MentorProfiles mp = profileByMentorId.get(m.getId());
            String skills = (mp != null && mp.getSkills() != null) ? mp.getSkills() : "";
            String bio = (mp != null && mp.getBiography() != null) ? mp.getBiography() : "";

            if (qLower != null && !qLower.isBlank()) {
                boolean matches = (m.getName() != null && m.getName().toLowerCase().contains(qLower))
                        || skills.toLowerCase().contains(qLower)
                        || bio.toLowerCase().contains(qLower);
                if (!matches) continue;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("id", m.getId());
            row.put("name", m.getName());
            row.put("email", m.getEmail());
            row.put("city", m.getCity());
            row.put("role", m.getRole());
            row.put("skills", skills);
            row.put("biografia", bio);
            row.put("score", 90);

            resp.add(row);
        }

        return ResponseEntity.ok(resp);
    }
}


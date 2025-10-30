package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.MentorProfiles;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.MentorProfilesService;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

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
    // Lee header "X-USER-ID" y trae info combinada de Users + MentorProfiles
    // =========================
    @GetMapping("/me/full-info")
    public ResponseEntity<Map<String, Object>> getMyFullInfo(
            @RequestHeader("X-USER-ID") Long mentorId
    ) {
        // Traer usuario base
        Users mentorUser = usersService.getUserById(mentorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mentor (Users) no encontrado"
                ));

        // Traer perfil detallado (bio, skills, etc.)
        MentorProfiles mentorProfile = mentorProfilesService
                .getOrCreateByMentorId(mentorId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", mentorUser.getId());
        resp.put("name", mentorUser.getName());
        resp.put("email", mentorUser.getEmail());
        resp.put("city", mentorUser.getCity());
        resp.put("role", mentorUser.getRole());

        // Campos específicos del perfil de mentor
        resp.put("disponibilidad",
                mentorProfile.getAvailability() != null ? mentorProfile.getAvailability() : "");
        resp.put("habilidades",
                mentorProfile.getSkills() != null ? mentorProfile.getSkills() : "");
        resp.put("biografia",
                mentorProfile.getBiography() != null ? mentorProfile.getBiography() : "");
        resp.put("rating",
                mentorProfile.getRating() != null ? mentorProfile.getRating() : 0);

        return ResponseEntity.ok(resp);
    }

    // =========================
    // PUT /mentors/me/full-info
    // Actualiza Users + MentorProfiles
    // =========================
    @PutMapping("/me/full-info")
    public ResponseEntity<?> updateMyFullInfo(
            @RequestHeader("X-USER-ID") Long mentorId,
            @RequestBody Map<String, Object> body
    ) {
        // 1. Traer usuario base (tabla Users)
        Users mentorUser = usersService.getUserById(mentorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mentor (Users) no encontrado"
                ));

        // 2. Traer perfil MentorProfiles (crear si no existe)
        MentorProfiles mentorProfile = mentorProfilesService
                .getOrCreateByMentorId(mentorId);

        // ---------- actualizar Users ----------
        if (body.containsKey("name")) {
            mentorUser.setName((String) body.get("name"));
        }
        if (body.containsKey("email")) {
            mentorUser.setEmail((String) body.get("email"));
        }
        if (body.containsKey("city")) {
            mentorUser.setCity((String) body.get("city"));
        }
        usersService.createUser(mentorUser); // reutilizamos tu método para guardar Users

        // ---------- actualizar MentorProfiles ----------
        if (body.containsKey("disponibilidad")) {
            mentorProfile.setAvailability((String) body.get("disponibilidad"));
        }
        if (body.containsKey("habilidades")) {
            mentorProfile.setSkills((String) body.get("habilidades"));
        }
        if (body.containsKey("biografia")) {
            mentorProfile.setBiography((String) body.get("biografia"));
        }
        // rating normalmente viene de reviews del estudiante, no lo tocaríamos acá.
        // Pero si quieres permitir edición manual, puedes descomentar:
        /*
        if (body.containsKey("rating")) {
            Object rawRating = body.get("rating");
            try {
                mentorProfile.setRating(Double.parseDouble(rawRating.toString()));
            } catch (Exception ignored) {}
        }
        */

        mentorProfilesService.saveProfileForMentor(mentorUser, mentorProfile);

        return ResponseEntity.ok("Perfil de mentor actualizado correctamente");
    }
}

package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.MentorInfo;
import com.matchMenthor.matchMenthor.Servicio.MentorInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentors")
@CrossOrigin(origins = "*")
public class MentorController {

    private final MentorInfoService mentorInfoService;

    public MentorController(MentorInfoService mentorInfoService) {
        this.mentorInfoService = mentorInfoService;
    }

    // Obtener info pública del mentor por userId
    @GetMapping("/{userId}/info")
    public ResponseEntity<MentorInfo> getMentorInfo(@PathVariable Long userId) {
        MentorInfo info = mentorInfoService.getMentorInfo(userId);
        return ResponseEntity.ok(info);
    }

    // Crear o actualizar info del mentor (disponibilidad, habilidades, biografia)
    @PutMapping("/{userId}/info")
    public ResponseEntity<MentorInfo> upsertMentorInfo(
            @PathVariable Long userId,
            @RequestBody MentorInfo body
    ) {
        MentorInfo saved = mentorInfoService.upsertMentorInfo(userId, body);
        return ResponseEntity.ok(saved);
    }
}

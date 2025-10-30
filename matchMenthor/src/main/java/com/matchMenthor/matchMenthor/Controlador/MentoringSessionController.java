package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.MentoringSession;
import com.matchMenthor.matchMenthor.Servicio.MentoringSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/mentors/me/sessions")
@CrossOrigin(origins = "*")
public class MentoringSessionController {

    private final MentoringSessionService service;

    public MentoringSessionController(MentoringSessionService service) {
        this.service = service;
    }

    // GET /mentors/me/sessions  -> lista de sesiones del mentor autenticado
    @GetMapping
    public ResponseEntity<?> getMySessions(@RequestHeader("X-USER-ID") Long mentorId) {
        List<MentoringSession> list = service.getForMentor(mentorId);

        // mapeo ligero para no mandar todo el árbol LAZY
        List<Map<String, Object>> dto = new ArrayList<>();
        for (MentoringSession s : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", s.getId());
            row.put("fecha", s.getSessionDate() != null ? s.getSessionDate().toString() : null);
            row.put("hora", s.getSessionTime() != null ? s.getSessionTime().toString() : null);
            row.put("estado", s.getStatus());
            row.put("temaPlaneado", s.getPlannedTopic());
            row.put("observacionesMentor", s.getMentorNotes());
            row.put("resumen", s.getSessionSummary());
            row.put("calificacionEstudianteAlMentor", s.getStudentRatingToMentor());
            row.put("calificacionMentorAlEstudiante", s.getMentorRatingToStudent());
            row.put("estudianteNombre",
                    (s.getStudent() != null) ? s.getStudent().getName() : "—");
            row.put("estudianteConfirmo", s.getStudentConfirmed());
            dto.add(row);
        }

        return ResponseEntity.ok(dto);
    }

    // POST /mentors/me/sessions  -> crear nueva sesión
    @PostMapping
    public ResponseEntity<?> createSession(
            @RequestHeader("X-USER-ID") Long mentorId,
            @RequestBody Map<String, Object> body
    ) {
        String date = (String) body.get("fecha");
        String time = (String) body.get("hora");
        String topic = (String) body.get("temaPlaneado");
        Long studentId = body.get("studentId") != null ? Long.valueOf(body.get("studentId").toString()) : null;
        Long matchId = body.get("matchId") != null ? Long.valueOf(body.get("matchId").toString()) : null;

        MentoringSession created = service.createForMentor(mentorId, studentId, matchId, date, time, topic);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", created.getId());
        resp.put("fecha", created.getSessionDate() != null ? created.getSessionDate().toString() : null);
        resp.put("hora", created.getSessionTime() != null ? created.getSessionTime().toString() : null);
        resp.put("estado", created.getStatus());
        resp.put("temaPlaneado", created.getPlannedTopic());
        resp.put("estudianteNombre", created.getStudent() != null ? created.getStudent().getName() : "—");
        resp.put("estudianteConfirmo", created.getStudentConfirmed());
        resp.put("observacionesMentor", created.getMentorNotes());
        resp.put("resumen", created.getSessionSummary());
        resp.put("calificacionEstudianteAlMentor", created.getStudentRatingToMentor());
        resp.put("calificacionMentorAlEstudiante", created.getMentorRatingToStudent());

        return ResponseEntity.ok(resp);
    }

    // PUT /mentors/me/sessions/{id}  -> actualizar observaciones, calificación, estado...
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(
            @RequestHeader("X-USER-ID") Long mentorId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        String notes = (String) body.get("observacionesMentor");
        String resumen = (String) body.get("resumen");
        String estado = (String) body.get("estado");
        Integer calMentor = null;
        if (body.get("calificacionMentorAlEstudiante") != null) {
            calMentor = Integer.valueOf(body.get("calificacionMentorAlEstudiante").toString());
        }

        MentoringSession updated = service.updateFeedback(
                mentorId,
                id,
                notes,
                calMentor,
                resumen,
                estado
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", updated.getId());
        resp.put("estado", updated.getStatus());
        resp.put("observacionesMentor", updated.getMentorNotes());
        resp.put("resumen", updated.getSessionSummary());
        resp.put("calificacionMentorAlEstudiante", updated.getMentorRatingToStudent());
        return ResponseEntity.ok(resp);
    }
}

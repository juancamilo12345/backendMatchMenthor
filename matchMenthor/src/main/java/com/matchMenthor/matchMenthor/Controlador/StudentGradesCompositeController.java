package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.StudentGrades;
import com.matchMenthor.matchMenthor.Modelo.Subjects;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.StudentGradesService;
import com.matchMenthor.matchMenthor.Servicio.SubjectsService;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/students/me/grades")
@CrossOrigin(origins = "*")
public class StudentGradesCompositeController {

    private final UsersService usersService;
    private final SubjectsService subjectsService;
    private final StudentGradesService studentGradesService;

    public StudentGradesCompositeController(UsersService usersService,
                                            SubjectsService subjectsService,
                                            StudentGradesService studentGradesService) {
        this.usersService = usersService;
        this.subjectsService = subjectsService;
        this.studentGradesService = studentGradesService;
    }

    @PostMapping
    public ResponseEntity<?> createMyGrade(
            @RequestHeader("X-USER-ID") Long studentId,
            @RequestBody Map<String, Object> body
    ) {
        String codigo = (String) body.get("codigo");
        String materia = (String) body.get("materia");
        Number notaNum = (Number) body.get("nota");
        String year = body.get("year") != null ? body.get("year").toString() : null;

        if (codigo == null || codigo.isBlank()
                || materia == null || materia.isBlank()
                || notaNum == null) {
            return ResponseEntity.badRequest().body("codigo, materia y nota son obligatorios");
        }

        Double nota = notaNum.doubleValue();

        Users student = usersService.getUserById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // busca por código o crea
        Subjects subject = subjectsService.findByCodeOrCreate(codigo, materia);

        StudentGrades grade = new StudentGrades();
        grade.setStudent(student);
        grade.setSubject(subject);
        grade.setGrade(nota);

        if (year != null && !year.isBlank()) {
            try {
                int y = Integer.parseInt(year);
                grade.setTakenAt(LocalDateTime.of(y, 1, 1, 0, 0));
            } catch (NumberFormatException e) {
                grade.setTakenAt(LocalDateTime.now());
            }
        } else {
            grade.setTakenAt(LocalDateTime.now());
        }

        StudentGrades saved = studentGradesService.create(grade);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", saved.getId());
        // 👇 AJUSTA ESTOS DOS GETTERS AL NOMBRE REAL DE TU ENTIDAD SUBJECTS
        resp.put("codigo", subject.getCode());
        resp.put("materia", subject.getSubjectName());
        resp.put("nota", saved.getGrade());
        resp.put("year", year);
        resp.put("studentId", studentId);

        return ResponseEntity.ok(resp);
    }
}

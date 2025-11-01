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
import java.util.*;

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

    // =========================================================
    // 1) LISTAR TODAS LAS NOTAS DEL ESTUDIANTE ACTUAL
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getMyGrades(
            @RequestHeader("X-USER-ID") Long studentId
    ) {
        // trae del servicio ya ordenadas
        List<StudentGrades> grades = studentGradesService.getByStudentId(studentId);

        List<Map<String, Object>> resp = new ArrayList<>();
        for (StudentGrades g : grades) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", g.getId());
            // subject puede ser null si algo falló al crear
            if (g.getSubject() != null) {
                row.put("codigo", g.getSubject().getCode());
                row.put("materia", g.getSubject().getSubjectName());
            } else {
                row.put("codigo", "");
                row.put("materia", "");
            }
            row.put("nota", g.getGrade());
            row.put("year", g.getTakenAt() != null ? g.getTakenAt().getYear() : null);
            resp.add(row);
        }

        return ResponseEntity.ok(resp);
    }

    // =========================================================
    // 2) CREAR NOTA (ya lo tenías)
    // =========================================================
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
        resp.put("codigo", subject.getCode());
        resp.put("materia", subject.getSubjectName());
        resp.put("nota", saved.getGrade());
        resp.put("year", year);
        resp.put("studentId", studentId);

        return ResponseEntity.ok(resp);
    }

    // =========================================================
    // 3) EDITAR UNA NOTA DEL ESTUDIANTE ACTUAL
    //    Aquí es donde te dejo cambiar NOTA + CÓDIGO + NOMBRE
    //    PUT /students/me/grades/{gradeId}
    // =========================================================
    @PutMapping("/{gradeId}")
    public ResponseEntity<?> updateMyGrade(
            @RequestHeader("X-USER-ID") Long studentId,
            @PathVariable Long gradeId,
            @RequestBody Map<String, Object> body
    ) {
        // 1. Traemos la nota
        StudentGrades grade = studentGradesService.getById(gradeId)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        // 2. Validamos que la nota sea del estudiante autenticado
        if (grade.getStudent() == null || !Objects.equals(grade.getStudent().getId(), studentId)) {
            return ResponseEntity.status(403).body("No puedes editar una nota de otro estudiante");
        }

        // 3. Actualizar la parte de la materia (codigo + nombre)
        String newCode = body.get("codigo") != null ? body.get("codigo").toString() : null;
        String newName = body.get("materia") != null ? body.get("materia").toString() : null;

        if (newCode != null && !newCode.isBlank()) {
            // si mando código, buscamos o creamos
            Subjects subject = subjectsService.findByCodeOrCreate(
                    newCode,
                    (newName != null && !newName.isBlank()) ? newName : "SIN NOMBRE"
            );
            grade.setSubject(subject);
        } else if (newName != null && !newName.isBlank() && grade.getSubject() != null) {
            // si solo cambió el nombre de la materia y ya había subject
            grade.getSubject().setSubjectName(newName);
            // OJO: si quieres guardar el subject, debería hacerlo el service de subjects
            subjectsService.save(grade.getSubject());
        }

        // 4. Actualizar la NOTA
        if (body.containsKey("nota")) {
            Number notaNum = (Number) body.get("nota");
            grade.setGrade(notaNum.doubleValue());
        }

        // 5. Actualizar el año (opcional)
        if (body.containsKey("year")) {
            String yearStr = body.get("year") != null ? body.get("year").toString() : null;
            if (yearStr != null && !yearStr.isBlank()) {
                try {
                    int y = Integer.parseInt(yearStr);
                    grade.setTakenAt(LocalDateTime.of(y, 1, 1, 0, 0));
                } catch (NumberFormatException e) {
                    // si falla, dejamos la fecha vieja
                }
            }
        }

        StudentGrades saved = studentGradesService.create(grade);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", saved.getId());
        resp.put("codigo", saved.getSubject() != null ? saved.getSubject().getCode() : "");
        resp.put("materia", saved.getSubject() != null ? saved.getSubject().getSubjectName() : "");
        resp.put("nota", saved.getGrade());
        resp.put("year", saved.getTakenAt() != null ? saved.getTakenAt().getYear() : null);

        return ResponseEntity.ok(resp);
    }

    // =========================================================
    // 4) ELIMINAR UNA NOTA DEL ESTUDIANTE ACTUAL
    // =========================================================
    @DeleteMapping("/{gradeId}")
    public ResponseEntity<?> deleteMyGrade(
            @RequestHeader("X-USER-ID") Long studentId,
            @PathVariable Long gradeId
    ) {
        StudentGrades grade = studentGradesService.getById(gradeId)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        if (grade.getStudent() == null || !Objects.equals(grade.getStudent().getId(), studentId)) {
            return ResponseEntity.status(403).body("No puedes borrar una nota de otro estudiante");
        }

        studentGradesService.delete(gradeId);
        return ResponseEntity.ok().build();
    }
}

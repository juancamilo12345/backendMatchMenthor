package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.StudentInfo;
import com.matchMenthor.matchMenthor.Servicio.StudentInfoService;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*") // permite requests desde tu front (Vite/React en otro puerto)
public class StudentController {

    private final StudentInfoService studentInfoService;
    private final UsersService usersService;

    public StudentController(StudentInfoService studentInfoService, UsersService usersService) {
        this.studentInfoService = studentInfoService;
        this.usersService = usersService;
    }

    // 🔸 Obtener info combinada (Users + StudentInfo)
    @GetMapping("/{userId}/full-info")
    public ResponseEntity<Map<String, Object>> getFullInfo(@PathVariable Long userId) {

        System.out.println("[BACKEND] => Id Recibido: " + userId);

        var user = usersService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("[BACKEND] => Usuario Encontrado: " + user.getName());

        var studentInfo = studentInfoService.getStudentInfo(userId);

        System.out.println("[BACKEND] => Estudiante Encontrado (Programa): " + studentInfo.getPrograma());

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("city", user.getCity());
        response.put("role", user.getRole());
        response.put("programa", studentInfo.getPrograma());
        response.put("semestre", studentInfo.getSemestre());

        return ResponseEntity.ok(response);
    }


    // 🔸 Obtener info combinada (Users + StudentInfo)
    @PutMapping("/{userId}/full-info")
    public ResponseEntity<?> updateFullInfo(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        var user = usersService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        var studentInfo = studentInfoService.getStudentInfo(userId);

        // actualizar info de Users
        if (body.containsKey("name")) user.setName((String) body.get("name"));
        if (body.containsKey("email")) user.setEmail((String) body.get("email"));
        if (body.containsKey("city")) user.setCity((String) body.get("city"));
        usersService.createUser(user);

        // actualizar info de StudentInfo
        if (body.containsKey("programa")) studentInfo.setPrograma((String) body.get("programa"));
        if (body.containsKey("semestre")) {
            Object semestre = body.get("semestre");
            if (semestre instanceof Number) {
                studentInfo.setSemestre(((Number) semestre).intValue());
            } else {
                try {
                    studentInfo.setSemestre(Integer.parseInt(semestre.toString()));
                } catch (NumberFormatException ignored) {}
            }
        }
        studentInfoService.createOrUpdateStudentInfo(user,studentInfo);

        return ResponseEntity.ok("Información actualizada correctamente");
    }
}

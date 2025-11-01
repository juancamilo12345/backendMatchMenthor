package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.StudentInfo;
import com.matchMenthor.matchMenthor.Modelo.Users;
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
@CrossOrigin(origins = "*") // permitir llamadas desde el front
public class StudentController {

    private final StudentInfoService studentInfoService;
    private final UsersService usersService;

    public StudentController(StudentInfoService studentInfoService,
                             UsersService usersService) {
        this.studentInfoService = studentInfoService;
        this.usersService = usersService;
    }

    // ----------------------------------------------------
    // GET /students/me/full-info
    // el front manda:  X-USER-ID: <id_user>
    // ----------------------------------------------------
    @GetMapping("/me/full-info")
    public ResponseEntity<Map<String, Object>> getMyFullInfo(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        Users user = usersService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        // Si no hay student_info, devolvemos uno vacío
        StudentInfo studentInfo = studentInfoService.getStudentInfo(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("city", user.getCity());
        response.put("role", user.getRole());

        response.put("programa",
                studentInfo.getPrograma() != null ? studentInfo.getPrograma() : "");
        response.put("semestre",
                studentInfo.getSemestre() != null ? studentInfo.getSemestre() : 0);

        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------
    // PUT /students/me/full-info
    // el front manda:  X-USER-ID: <id_user>
    // y en el body: { name, email, city, programa, semestre }
    // ----------------------------------------------------
    @PutMapping("/me/full-info")
    public ResponseEntity<?> updateMyFullInfo(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody Map<String, Object> body
    ) {
        // 1. Traer el usuario REAL que existe en la BD
        Users currentUser = usersService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        // 2. Preparar un Users parcial SOLO con los campos que quiero cambiar
        Users toUpdate = new Users();
        // muy importante: conservar rol y bloqueado
        toUpdate.setRole(currentUser.getRole());
        toUpdate.setBlocked(currentUser.isBlocked());

        if (body.containsKey("name")) {
            toUpdate.setName((String) body.get("name"));
        }
        if (body.containsKey("email")) {
            // si quieres impedir cambiar email, elimina este bloque
            toUpdate.setEmail((String) body.get("email"));
        }
        if (body.containsKey("city")) {
            toUpdate.setCity((String) body.get("city"));
        }

        // 👉 aquí usamos UPDATE, NO createUser
        usersService.updateUser(userId, toUpdate);

        // 3. Traer o crear la info académica
        StudentInfo studentInfo = studentInfoService.getStudentInfo(userId);

        if (body.containsKey("programa")) {
            studentInfo.setPrograma((String) body.get("programa"));
        }

        if (body.containsKey("semestre")) {
            Object semestreRaw = body.get("semestre");
            if (semestreRaw instanceof Number) {
                studentInfo.setSemestre(((Number) semestreRaw).intValue());
            } else if (semestreRaw != null) {
                try {
                    studentInfo.setSemestre(Integer.parseInt(semestreRaw.toString()));
                } catch (NumberFormatException ignored) {
                    // si viene algo raro, lo dejamos como está
                }
            }
        }

        // asegurar que quede ligado al usuario
        studentInfo.setUser(currentUser);
        studentInfoService.createOrUpdateStudentInfo(currentUser, studentInfo);

        return ResponseEntity.ok("Información actualizada correctamente");
    }
}

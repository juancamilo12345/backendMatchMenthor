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
@CrossOrigin(origins = "*") // permitir llamadas desde el front
public class StudentController {

    private final StudentInfoService studentInfoService;
    private final UsersService usersService;

    public StudentController(StudentInfoService studentInfoService, UsersService usersService) {
        this.studentInfoService = studentInfoService;
        this.usersService = usersService;
    }

    // ------------- GET PERFIL DEL USUARIO ACTUAL -------------
    // Front envia header:  X-USER-ID: 12
    // y NO mando nada en localStorage
    @GetMapping("/me/full-info")
    public ResponseEntity<Map<String, Object>> getMyFullInfo(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        var user = usersService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

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

    // ------------- PUT PERFIL DEL USUARIO ACTUAL -------------
    // Front envia header:  X-USER-ID: 12
    // y body con los campos editados
    @PutMapping("/me/full-info")
    public ResponseEntity<?> updateMyFullInfo(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody Map<String, Object> body
    ) {
        // 1. Traer usuario base
        var user = usersService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        // 2. Traer o crear info académica asociada
        var studentInfo = studentInfoService.getStudentInfo(userId);

        // -------- actualizar datos Users --------
        if (body.containsKey("name")) {
            user.setName((String) body.get("name"));
        }
        if (body.containsKey("email")) {
            user.setEmail((String) body.get("email"));
        }
        if (body.containsKey("city")) {
            user.setCity((String) body.get("city"));
        }
        usersService.createUser(user); // persiste Users

        // -------- actualizar datos StudentInfo --------
        if (body.containsKey("programa")) {
            studentInfo.setPrograma((String) body.get("programa"));
        }

        if (body.containsKey("semestre")) {
            Object semestreRaw = body.get("semestre");
            if (semestreRaw instanceof Number) {
                studentInfo.setSemestre(((Number) semestreRaw).intValue());
            } else {
                try {
                    studentInfo.setSemestre(Integer.parseInt(semestreRaw.toString()));
                } catch (NumberFormatException ignored) {}
            }
        }

        studentInfoService.createOrUpdateStudentInfo(user, studentInfo);

        return ResponseEntity.ok("Información actualizada correctamente");
    }
}



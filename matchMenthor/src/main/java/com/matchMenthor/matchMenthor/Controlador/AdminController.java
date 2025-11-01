package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.AdminService;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final UsersService usersService;

    public AdminController(AdminService adminService,
                           UsersService usersService) {
        this.adminService = adminService;
        this.usersService = usersService;
    }

    // =========================
    // LISTADOS
    // =========================

    @GetMapping("/students")
    public List<Users> getAllStudents() {
        return adminService.getAllStudents();
    }

    @GetMapping("/mentors")
    public List<Users> getAllMentors() {
        return adminService.getAllMentors();
    }

    @GetMapping("/users")
    public List<Users> getAllUsers() {
        return adminService.getAllUsers();
    }

    // =========================
    // CREAR USUARIO
    // POST /admin/users
    // =========================
    @PostMapping("/users")
    public ResponseEntity<Users> createUser(@RequestBody Map<String, Object> body) {
        Users u = new Users();
        u.setName((String) body.get("name"));
        u.setEmail((String) body.get("email"));
        u.setCity((String) body.get("city"));

        // role viene en texto: STUDENT | MENTOR | ADMIN? (tú solo tienes STUDENT, MENTOR)
        String roleStr = (String) body.get("role");
        if (roleStr != null) {
            u.setRole(Users.Role.valueOf(roleStr));
        } else {
            u.setRole(Users.Role.STUDENT);
        }

        // password por defecto
        String password = (String) body.getOrDefault("password", "123456");
        u.setPassword(password);

        // por defecto NO bloqueado
        u.setBlocked(false);

        Users saved = usersService.createUser(u);
        return ResponseEntity.ok(saved);
    }

    // =========================
    // EDITAR USUARIO
    // PUT /admin/users/{id}
    // =========================
    @PutMapping("/users/{id}")
    public ResponseEntity<Users> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Users existing = usersService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (body.containsKey("name")) {
            existing.setName((String) body.get("name"));
        }
        if (body.containsKey("email")) {
            existing.setEmail((String) body.get("email"));
        }
        if (body.containsKey("city")) {
            existing.setCity((String) body.get("city"));
        }
        if (body.containsKey("role")) {
            String roleStr = (String) body.get("role");
            existing.setRole(Users.Role.valueOf(roleStr));
        }
        if (body.containsKey("password")) {
            existing.setPassword((String) body.get("password"));
        }
        if (body.containsKey("blocked")) {
            // por si lo editas desde un panel
            boolean blocked = Boolean.parseBoolean(body.get("blocked").toString());
            existing.setBlocked(blocked);
        }

        Users saved = usersService.createUser(existing);
        return ResponseEntity.ok(saved);
    }

    // =========================
    // ELIMINAR USUARIO
    // DELETE /admin/users/{id}
    // =========================
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // =========================
    // BLOQUEAR / DESBLOQUEAR USUARIO
    // PATCH /admin/users/{id}/block?blocked=true
    // =========================
    @PatchMapping("/users/{id}/block")
    public ResponseEntity<Users> blockUser(
            @PathVariable Long id,
            @RequestParam(name = "blocked", required = false) Boolean blocked,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        Users u = usersService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean finalBlocked;
        if (blocked != null) {
            finalBlocked = blocked;
        } else if (body != null && body.get("blocked") != null) {
            finalBlocked = Boolean.parseBoolean(body.get("blocked").toString());
        } else {
            // si no mandas nada → lo bloqueo
            finalBlocked = true;
        }

        u.setBlocked(finalBlocked);
        Users saved = usersService.createUser(u);
        return ResponseEntity.ok(saved);
    }
}


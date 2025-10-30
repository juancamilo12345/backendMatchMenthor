package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable Long id) {
        return usersService.getUserById(id).orElseThrow();
    }

    // ============================
    // REGISTRO / CREAR USUARIO
    // ============================
    @PostMapping
    public Users createUser(@RequestBody Map<String, Object> body) {
        // armamos un Users a partir del JSON del front
        Users u = new Users();
        u.setName((String) body.get("name"));
        u.setEmail((String) body.get("email"));
        u.setPassword((String) body.get("password"));
        u.setCity((String) body.get("city"));

        // normalizar rol porque tu enum es SOLO STUDENT y MENTOR
        Object roleObj = body.get("role");
        if (roleObj != null) {
            String roleStr = roleObj.toString().toUpperCase(); // "student" -> "STUDENT"
            if ("MENTOR".equals(roleStr)) {
                u.setRole(Users.Role.MENTOR);
            } else {
                u.setRole(Users.Role.STUDENT);
            }
        } else {
            // si no mandan rol -> student
            u.setRole(Users.Role.STUDENT);
        }

        // delegar la validación al service (correo, duplicado, bloqueado, etc.)
        return usersService.createUser(u);
    }

    // ============================
    // ACTUALIZAR
    // ============================
    @PutMapping("/{id}")
    public Users updateUser(@PathVariable Long id, @RequestBody Users user) {
        return usersService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
    }

    @GetMapping("/students")
    public List<Users> getStudents() {
        return usersService.getAllStudents();
    }

    @GetMapping("/mentors")
    public List<Users> getMentors() {
        return usersService.getAllMentors();
    }
}

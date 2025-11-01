package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.DTO.UserListDto;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.AdminService;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService usersService;
    private final AdminService adminService;

    public UsersController(UsersService usersService,
                           AdminService adminService) {
        this.usersService = usersService;
        this.adminService = adminService;
    }

    // ============================
    // GET /users   (todos - entidad completa)
    // ============================
    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    // ============================
    // GET /users/students   (DTO plano)
    // ============================
    @GetMapping("/students")
    public List<UserListDto> getStudents() {
        return usersService.getAllStudents()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ============================
    // GET /users/mentors     (DTO plano)
    // ============================
    @GetMapping("/mentors")
    public List<UserListDto> getMentors() {
        return usersService.getAllMentors()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ============================
    // GET /users/admins      (DTO plano)
    // ============================
    @GetMapping("/admins")
    public List<UserListDto> getAdmins() {
        return usersService.getAllAdmins()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ============================
    // GET /users/{id}
    // ============================
    @GetMapping("/{id}")
    public Users getUserById(@PathVariable Long id) {
        return usersService.getUserById(id).orElseThrow();
    }

    // ============================
    // POST /users  (REGISTRO)
    // ============================
    @PostMapping
    public Users createUser(@RequestBody Map<String, Object> body) {

        Users u = new Users();
        u.setName((String) body.get("name"));
        u.setEmail((String) body.get("email"));
        u.setPassword((String) body.get("password"));
        u.setCity((String) body.get("city"));

        // normalizar rol
        String roleStr = (body.get("role") != null)
                ? body.get("role").toString().toUpperCase()
                : "STUDENT";

        switch (roleStr) {
            case "ADMIN":
                String adminCode = (String) body.get("adminCode");
                if (adminCode == null || !adminCode.equals("CODIGO-SECRETO-123")) {
                    throw new RuntimeException("Código de administrador inválido");
                }
                u.setRole(Users.Role.ADMIN);
                break;
            case "MENTOR":
                u.setRole(Users.Role.MENTOR);
                break;
            default:
                u.setRole(Users.Role.STUDENT);
        }

        // guardar en users
        Users saved = usersService.createUser(u);

        // si es admin -> también lo guardamos en tabla admins
        if (saved.getRole() == Users.Role.ADMIN) {
            adminService.createFromUser(saved);
        }

        return saved;
    }

    // ============================
    // PUT /users/{id}
    // ============================
    @PutMapping("/{id}")
    public Users updateUser(@PathVariable Long id, @RequestBody Users user) {
        return usersService.updateUser(id, user);
    }

    // ============================
    // DELETE /users/{id}
    // ============================
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
    }

    // ============================
    // MAPPER entidad -> dto
    // ============================
    private UserListDto toDto(Users u) {
        if (u == null) return null;
        return new UserListDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().name() : null,
                u.getCity(),
                u.isBlocked(),
                u.getCreatedAt()
        );
    }
}

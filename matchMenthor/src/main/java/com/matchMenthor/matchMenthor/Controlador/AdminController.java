package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*") // si accedes desde frontend
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

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
}


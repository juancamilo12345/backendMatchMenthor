package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Modelo.Admin;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import com.matchMenthor.matchMenthor.Repositorio.AdminRepository;
import com.matchMenthor.matchMenthor.Servicio.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    // ✅ 1. LOGIN
    @PostMapping("/login")
    public String login(@RequestParam String rol, @RequestParam String email, @RequestParam String password) {
        return authService.login(rol, email, password);
    }

    // ✅ 2. REGISTRO DE USUARIO
    @PostMapping("/register-user")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String city) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "El correo ya está registrado";
        }

        Users user = new Users();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setCity(city);
        user.setRole(Users.Role.STUDENT);
        user.setBloqueado(false);
        user.setIntentosFallidos(0);

        userRepository.save(user);
        return "Usuario registrado correctamente";
    }

    // ✅ 3. REGISTRO DE ADMINISTRADOR
    @PostMapping("/register-admin")
    public String registerAdmin(@RequestParam String nombre,
                                @RequestParam String email,
                                @RequestParam String password) {
        if (adminRepository.findByEmail(email).isPresent()) {
            return "El correo ya está registrado";
        }

        Admin admin = new Admin();
        admin.setName(nombre);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.setBloqueadoAdmin(false);
        admin.setIntentosFallidosAdmin(0);

        adminRepository.save(admin);
        return "Administrador registrado correctamente";
    }
}

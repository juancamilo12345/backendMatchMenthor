package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Servicio.LoginService;
import com.matchMenthor.matchMenthor.Servicio.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*")
public class LoginController {

    private final LoginService authService;

    public LoginController(LoginService LoginService) {
        this.authService = LoginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        // delegamos toda la lógica en el service
        return authService.login(email, password);
    }
}

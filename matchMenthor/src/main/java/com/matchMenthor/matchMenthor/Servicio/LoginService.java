package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class LoginService {

    private final UserRepository userRepository;

    // mapa en memoria: email -> intentos fallidos
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();

    // regex simple de email
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^.+@.+\\..+$");

    private static final int MAX_ATTEMPTS = 3;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> login(String email, String password) {

        // 1. Validar que vengan datos
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "El correo es obligatorio"
                    ));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "La contraseña es obligatoria"
                    ));
        }

        // 2. Validar formato de email
        if (!EMAIL_REGEX.matcher(email).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "El correo no tiene un formato válido"
                    ));
        }

        // 3. Revisar si ya está bloqueado por intentos
        int currentAttempts = loginAttempts.getOrDefault(email, 0);
        if (currentAttempts >= MAX_ATTEMPTS) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS) // 429
                    .body(Map.of(
                            "error", "Has superado el número máximo de intentos. Intenta más tarde.",
                            "locked", true
                    ));
        }

        // 4. Buscar usuario por email
        Optional<Users> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            increaseAttempts(email, currentAttempts);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Usuario o contraseña incorrectos",
                            "attempts", currentAttempts + 1,
                            "maxAttempts", MAX_ATTEMPTS
                    ));
        }

        Users user = optUser.get();

        // 5. Validar password (aquí deberías usar bcrypt, por ahora plano)
        if (!password.equals(user.getPassword())) {
            increaseAttempts(email, currentAttempts);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Usuario o contraseña incorrectos",
                            "attempts", currentAttempts + 1,
                            "maxAttempts", MAX_ATTEMPTS
                    ));
        }

        // 6. Si llegó aquí: login correcto → limpiar intentos
        loginAttempts.remove(email);

        // 7. Respuesta OK
        return ResponseEntity.ok(Map.of(
                "token", "fake-jwt-token-123",
                "userId", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }

    private void increaseAttempts(String email, int currentAttempts) {
        loginAttempts.put(email, currentAttempts + 1);
    }
}


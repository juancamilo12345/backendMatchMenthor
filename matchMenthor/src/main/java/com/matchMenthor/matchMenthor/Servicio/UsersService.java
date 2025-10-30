package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UsersService {

    private final UserRepository userRepository;

    // regex sencillo para validar correos
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^.+@.+\\..+$");

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =============================
    // LECTURAS
    // =============================
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<Users> getAllStudents() {
        return userRepository.findByRole(Users.Role.STUDENT);
    }

    public List<Users> getAllMentors() {
        return userRepository.findByRole(Users.Role.MENTOR);
    }

    // =============================
    // CREAR (con validaciones)
    // =============================
    public Users createUser(Users user) {

        // 1. validar obligatorios
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
        }
        if (!EMAIL_REGEX.matcher(user.getEmail()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no es válido");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        if (user.getCity() == null || user.getCity().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ciudad es obligatoria");
        }

        // 2. ¿ya existe un usuario con ese correo?
        // aquí SÍ usamos findByEmail porque queremos saber si está bloqueado
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existing -> {
                    if (existing.isBlocked()) {
                        // existe y está bloqueado
                        throw new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "Este usuario está bloqueado. Contacta al administrador."
                        );
                    }
                    // existe y no está bloqueado
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Ya existe un usuario con ese correo"
                    );
                });

        // 3. rol por defecto
        if (user.getRole() == null) {
            user.setRole(Users.Role.STUDENT);
        }

        // 4. por defecto no bloqueado
        user.setBlocked(false);

        // 5. guardar
        return userRepository.save(user);
    }

    // =============================
    // ACTUALIZAR
    // =============================
    public Users updateUser(Long id, Users newUser) {
        return userRepository.findById(id)
                .map(user -> {
                    // si cambian el correo, validar que no esté usado por otro
                    if (newUser.getEmail() != null && !newUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                        // ¿ya lo tiene otro usuario?
                        userRepository.findByEmail(newUser.getEmail())
                                .ifPresent(existing -> {
                                    if (!existing.getId().equals(id)) {
                                        throw new ResponseStatusException(
                                                HttpStatus.CONFLICT,
                                                "Ya existe un usuario con ese correo"
                                        );
                                    }
                                });

                        // validar formato también
                        if (!EMAIL_REGEX.matcher(newUser.getEmail()).matches()) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no es válido");
                        }

                        user.setEmail(newUser.getEmail());
                    }

                    if (newUser.getName() != null) {
                        user.setName(newUser.getName());
                    }
                    if (newUser.getPassword() != null) {
                        user.setPassword(newUser.getPassword());
                    }
                    if (newUser.getCity() != null) {
                        user.setCity(newUser.getCity());
                    }
                    if (newUser.getRole() != null) {
                        user.setRole(newUser.getRole());
                    }

                    // permitir actualizar bloqueado desde admin
                    user.setBlocked(newUser.isBlocked());

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // =============================
    // ELIMINAR
    // =============================
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // =============================
    // EXTRA: bloquear / desbloquear
    // =============================
    public void setBlocked(Long id, boolean blocked) {
        Users u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setBlocked(blocked);
        userRepository.save(u);
    }
}

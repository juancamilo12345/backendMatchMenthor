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

    // ✅ versión más permisiva: solo exige que exista un "@"
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^.+@.+$");

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

    public List<Users> getAllAdmins() {
        return userRepository.findByRole(Users.Role.ADMIN);
    }

    // =============================
    // CREAR (con validaciones) o GUARDAR USUARIO EXISTENTE
    // =============================
    public Users createUser(Users user) {
        // 1. normalizar un poquito
        if (user.getEmail() != null) user.setEmail(user.getEmail().trim());
        if (user.getName() != null) user.setName(user.getName().trim());
        if (user.getCity() != null) user.setCity(user.getCity().trim());

        // 2. validar obligatorios
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
        }
        if (!EMAIL_REGEX.matcher(user.getEmail()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no es válido");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        if (user.getCity() == null || user.getCity().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ciudad es obligatoria");
        }

        // 3. evitar conflicto de correo duplicado,
        //    PERO permitir cuando es el mismo usuario que se está editando
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existing -> {
                    // si estoy creando (id == null) o el correo pertenece a OTRO id -> conflicto
                    if (user.getId() == null || !existing.getId().equals(user.getId())) {
                        if (existing.isBlocked()) {
                            throw new ResponseStatusException(
                                    HttpStatus.FORBIDDEN,
                                    "Este usuario está bloqueado. Contacta al administrador."
                            );
                        }
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe un usuario con ese correo"
                        );
                    }
                });

        // 4. asignar valores por defecto
        if (user.getRole() == null) {
            user.setRole(Users.Role.STUDENT);
        }
        // como es boolean primitivo, simplemente lo dejamos como venga.
        // si tu entidad lo tiene como Boolean, aquí sí podrías hacer el null-check.
        // user.setBlocked(user.isBlocked()); // esto es redundante, pero válido

        // 5. guardar
        return userRepository.save(user);
    }

    // =============================
    // ACTUALIZAR
    // =============================
    public Users updateUser(Long id, Users newUser) {
        return userRepository.findById(id)
                .map(user -> {

                    // normalizar lo que venga
                    if (newUser.getEmail() != null) newUser.setEmail(newUser.getEmail().trim());
                    if (newUser.getName() != null) newUser.setName(newUser.getName().trim());
                    if (newUser.getCity() != null) newUser.setCity(newUser.getCity().trim());

                    // si cambian el correo
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

                        // validar formato (más permisivo)
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

                    // permitir cambiar rol (STUDENT, MENTOR, ADMIN)
                    if (newUser.getRole() != null) {
                        user.setRole(newUser.getRole());
                    }

                    // permitir actualizar bloqueado (solo si lo mandan)
                    // aquí asumimos que newUser.isBlocked() es boolean primitivo;
                    // si quieres que solo cambie cuando venga en el body, cambia a Boolean en la entidad.
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

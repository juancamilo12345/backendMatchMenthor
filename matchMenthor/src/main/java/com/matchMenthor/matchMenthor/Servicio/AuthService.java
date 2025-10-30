package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Admin;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.AdminRepository;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private AdminRepository adminsRepository;

    public String login(String rol, String email, String password) {
        if (!rol.equalsIgnoreCase("admin") &&
                !rol.equalsIgnoreCase("student") &&
                !rol.equalsIgnoreCase("mentor")) {
            return "Rol no válido. Use 'admin', 'student' o 'mentor'";
        }

        if (rol.equalsIgnoreCase("admin")) {
            Optional<Admin> adminOpt = adminsRepository.findByEmail(email);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                return verificarCredenciales(admin, password);
            }
            return "Administrador no encontrado";
        }

        // Si es estudiante o mentor, se busca en users
        Optional<Users> userOpt = usersRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return verificarCredenciales(user, password);
        }

        return "Usuario no encontrado";
    }



    private <T> String verificarCredenciales(T entity, String password) {
        if (entity instanceof Users user) {

            // 🔹 Verificar si está bloqueado
            if (user.isBloqueado()) {
                if (user.getFechaBloqueo() != null) {
                    long minutos = Duration.between(user.getFechaBloqueo(), LocalDateTime.now()).toMinutes();
                    if (minutos >= 1) {
                        // Desbloquear automáticamente
                        user.setBloqueado(false);
                        user.setIntentosFallidos(0);
                        user.setFechaBloqueo(null);
                        usersRepository.save(user);
                    } else {
                        return "Cuenta bloqueada temporalmente. Intenta en " + (1 - minutos) + " minutos.";
                    }
                } else {
                    return "Cuenta bloqueada. Contacta al administrador.";
                }
            }

            return validarLogin(user.getPassword(), password, user, usersRepository);

        } else if (entity instanceof Admin admin) {

            // 🔹 Verificar si está bloqueado
            if (admin.isBloqueadoAdmin()) {
                if (admin.getFechaBloqueo() != null) {
                    long minutos = Duration.between(admin.getFechaBloqueo(), LocalDateTime.now()).toMinutes();
                    if (minutos >= 1) {
                        // Desbloquear automáticamente
                        admin.setBloqueadoAdmin(false);
                        admin.setIntentosFallidosAdmin(0);
                        admin.setFechaBloqueo(null);
                        adminsRepository.save(admin);
                    } else {
                        return "Cuenta de administrador bloqueada. Intenta en " + (1 - minutos) + " minutos.";
                    }
                } else {
                    return "Cuenta de administrador bloqueada. Contacta soporte.";
                }
            }

            return validarLogin(admin.getPassword(), password, admin, adminsRepository);
        }

        return "Error en la autenticación";
    }


    private <E> String validarLogin(String storedPassword, String inputPassword, E entity, JpaRepository<E, Long> repo) {
        boolean isUser = entity instanceof Users;
        boolean isAdmin = entity instanceof Admin;

        if (storedPassword.equals(inputPassword)) {
            if (isUser) {
                Users u = (Users) entity;
                u.setIntentosFallidos(0);
                u.setBloqueado(false);
            } else if (isAdmin) {
                Admin a = (Admin) entity;
                a.setIntentosFallidosAdmin(0);
                a.setBloqueadoAdmin(false);
            }
            repo.save(entity);
            return "Login exitoso";
        } else {
            manejarIntentos(entity, repo);
            return "Contraseña incorrecta";
        }
    }


    private <E> void manejarIntentos(E entity, JpaRepository<E, Long> repo) {
        if (entity instanceof Users u) {
            int intentos = u.getIntentosFallidos() + 1;
            if (intentos >= 3) {
                u.setBloqueado(true);
                u.setFechaBloqueo(LocalDateTime.now());
            }
            u.setIntentosFallidos(intentos);
            repo.save(entity);
        } else if (entity instanceof Admin a) {
            int intentos = a.getIntentosFallidosAdmin() + 1;
            if (intentos >= 3) {
                a.setBloqueadoAdmin(true);
                a.setFechaBloqueo(LocalDateTime.now());
            }
            a.setIntentosFallidosAdmin(intentos);
            repo.save(entity);
        }

    }
}
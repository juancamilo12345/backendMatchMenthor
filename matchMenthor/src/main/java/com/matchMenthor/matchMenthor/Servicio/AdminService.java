package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Admin;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.AdminRepository;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AdminService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    // =====================================================
    // 1️⃣ Registrar también en tabla admins
    // =====================================================
    public Admin createFromUser(Users user) {
        // Validar si ya existe
        if (adminRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un administrador con ese correo.");
        }

        Admin admin = new Admin();
        admin.setName(user.getName());
        admin.setEmail(user.getEmail());
        admin.setPassword(user.getPassword());
        admin.setLastAction("Creado desde registro de usuario");
        return adminRepository.save(admin);
    }

    // =====================================================
    // 2️⃣ Listados
    // =====================================================
    public List<Users> getAllStudents() {
        return userRepository.findByRole(Users.Role.STUDENT);
    }

    public List<Users> getAllMentors() {
        return userRepository.findByRole(Users.Role.MENTOR);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
}

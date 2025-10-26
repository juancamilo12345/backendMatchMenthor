package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Users> getAllStudents() {
        return userRepository.findByRole(Users.Role.STUDENT);
    }

    public List<Users> getAllMentors() {
        return userRepository.findByRole(Users.Role.MENTOR);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
}


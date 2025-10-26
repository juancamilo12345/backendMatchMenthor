package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UserRepository userRepository;

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    public Users updateUser(Long id, Users newUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    user.setEmail(newUser.getEmail());
                    user.setPassword(newUser.getPassword());
                    user.setCity(newUser.getCity());
                    user.setRole(newUser.getRole());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<Users> getAllStudents() {
        return userRepository.findByRole(Users.Role.STUDENT);
    }

    public List<Users> getAllMentors() {
        return userRepository.findByRole(Users.Role.MENTOR);
    }
}

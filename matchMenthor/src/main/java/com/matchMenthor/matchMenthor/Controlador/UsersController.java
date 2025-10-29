package com.matchMenthor.matchMenthor.Controlador;

import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Servicio.UsersService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable Long id) {
        return usersService.getUserById(id).orElseThrow();
    }

    @PostMapping
    public Users createUser(@RequestBody Users user) {
        return usersService.createUser(user);
    }


    @PutMapping("/{id}")
    public Users updateUser(@PathVariable Long id, @RequestBody Users user) {
        return usersService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
    }

    @GetMapping("/students")
    public List<Users> getStudents() {
        return usersService.getAllStudents();
    }

    @GetMapping("/mentors")
    public List<Users> getMentors() {
        return usersService.getAllMentors();
    }
}

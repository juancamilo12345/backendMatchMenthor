package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.StudentInfo;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.StudentInfoRepository;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;
    private final UserRepository usersRepository;

    public StudentInfoService(StudentInfoRepository studentInfoRepository,
                              UserRepository usersRepository) {
        this.studentInfoRepository = studentInfoRepository;
        this.usersRepository = usersRepository;
    }

    // Obtener info estudiante por id de usuario
    public StudentInfo getStudentInfo(Long userId) {
        return studentInfoRepository.findByUserId(userId)
                .orElse(new StudentInfo());
    }

    // Crear o actualizar info estudiante
    public StudentInfo upsertStudentInfo(Long userId, StudentInfo body) {
        // buscar usuario
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        // validar rol
        if (user.getRole() != Users.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario no es STUDENT, no se puede guardar StudentInfo"
            );
        }

        // si ya hay StudentInfo, lo reutilizamos (update), si no creamos uno nuevo
        StudentInfo info = studentInfoRepository.findByUserId(userId)
                .orElseGet(StudentInfo::new);

        // asignar/actualizar campos permitidos
        info.setUser(user);
        info.setPrograma(body.getPrograma());
        info.setSemestre(body.getSemestre());

        // guardar
        return studentInfoRepository.save(info);
    }

    // Crear un nuevo usuario con rol STUDENT y su StudentInfo asociado
    public StudentInfo createOrUpdateStudentInfo(Users user, StudentInfo infoBody) {
        StudentInfo info = studentInfoRepository.findByUserId(user.getId())
                .orElse(new StudentInfo());

        info.setUser(user);
        info.setPrograma(infoBody.getPrograma());
        info.setSemestre(infoBody.getSemestre());

        return studentInfoRepository.save(info);
    }


}

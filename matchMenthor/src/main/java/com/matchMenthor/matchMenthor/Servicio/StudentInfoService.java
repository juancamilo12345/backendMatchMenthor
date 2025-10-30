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

    // Traer info académica del estudiante por id de usuario.
    // Si no existe, devolvemos uno vacío en vez de null
    public StudentInfo getStudentInfo(Long userId) {
        return studentInfoRepository.findByUserId(userId)
                .orElse(new StudentInfo());
    }

    // (lo dejamos para usos futuros tipo POST /students/{id}/info)
    public StudentInfo upsertStudentInfo(Long userId, StudentInfo body) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        if (user.getRole() != Users.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario no es STUDENT, no se puede guardar StudentInfo"
            );
        }

        StudentInfo info = studentInfoRepository.findByUserId(userId)
                .orElseGet(StudentInfo::new);

        info.setUser(user);
        info.setPrograma(body.getPrograma());
        info.setSemestre(body.getSemestre());

        return studentInfoRepository.save(info);
    }

    // usado por el PUT del controlador
    public StudentInfo createOrUpdateStudentInfo(Users user, StudentInfo infoBody) {
        StudentInfo info = studentInfoRepository.findByUserId(user.getId())
                .orElse(new StudentInfo());

        info.setUser(user);
        info.setPrograma(infoBody.getPrograma());
        info.setSemestre(infoBody.getSemestre());

        return studentInfoRepository.save(info);
    }
}



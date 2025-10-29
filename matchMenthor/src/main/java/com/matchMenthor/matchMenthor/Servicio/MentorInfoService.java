package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.MentorInfo;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.MentorInfoRepository;
import com.matchMenthor.matchMenthor.Repositorio.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MentorInfoService {

    private final MentorInfoRepository mentorInfoRepository;
    private final UserRepository usersRepository;

    public MentorInfoService(MentorInfoRepository mentorInfoRepository,
                             UserRepository usersRepository) {
        this.mentorInfoRepository = mentorInfoRepository;
        this.usersRepository = usersRepository;
    }

    // Obtener info mentor por id de usuario
    public MentorInfo getMentorInfo(Long userId) {
        return mentorInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe información de mentor para este usuario"
                ));
    }

    // Crear o actualizar info mentor
    public MentorInfo upsertMentorInfo(Long userId, MentorInfo body) {
        // buscar usuario
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        // validar rol
        if (user.getRole() != Users.Role.MENTOR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario no es MENTOR, no se puede guardar MentorInfo"
            );
        }

        // si ya hay MentorInfo, lo reutilizamos, si no creamos uno nuevo
        MentorInfo info = mentorInfoRepository.findByUserId(userId)
                .orElseGet(MentorInfo::new);

        // actualizar campos
        info.setUser(user);
        info.setDisponibilidad(body.getDisponibilidad());
        info.setHabilidades(body.getHabilidades());
        info.setBiografia(body.getBiografia());

        // guardar
        return mentorInfoRepository.save(info);
    }
}

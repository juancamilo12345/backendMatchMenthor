package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.MentorProfiles;
import com.matchMenthor.matchMenthor.Modelo.Users;
import com.matchMenthor.matchMenthor.Repositorio.MentorProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MentorProfilesService {

    private final MentorProfileRepository repository;

    public MentorProfilesService(MentorProfileRepository repository) {
        this.repository = repository;
    }

    // CRUD genérico (lo mantenemos)
    public List<MentorProfiles> getAll() {
        return repository.findAll();
    }

    public Optional<MentorProfiles> getById(Long id) {
        return repository.findById(id);
    }

    public MentorProfiles create(MentorProfiles profile) {
        return repository.save(profile);
    }

    public MentorProfiles update(Long id, MentorProfiles newProfile) {
        return repository.findById(id)
                .map(profile -> {
                    profile.setBiography(newProfile.getBiography());
                    profile.setSkills(newProfile.getSkills());
                    profile.setAvailability(newProfile.getAvailability());
                    profile.setRating(newProfile.getRating());
                    // OJO: aquí normalmente no cambiamos el mentor asociado
                    return repository.save(profile);
                })
                .orElseThrow(() -> new RuntimeException("Perfil de mentor no encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ============================
    // NUEVO: obtener perfil por id de mentor. Si no existe, devolver uno vacío EN MEMORIA
    // (para que el controlador no truene con null).
    // ============================
    public MentorProfiles getOrCreateByMentorId(Long mentorId) {
        return repository.findByMentor_Id(mentorId)
                .orElseGet(MentorProfiles::new);
    }

    // ============================
    // NUEVO: guardar/actualizar asegurando que esté ligado al mentor correcto
    // ============================
    public MentorProfiles saveProfileForMentor(Users mentorUser, MentorProfiles profile) {
        // MUY IMPORTANTE:
        // Nos aseguramos que el perfil tenga asociado el mentor correcto
        profile.setMentor(mentorUser);
        return repository.save(profile);
    }
}

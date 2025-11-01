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
                    return repository.save(profile);
                })
                .orElseThrow(() -> new RuntimeException("Perfil de mentor no encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ✅ Nuevo método: crea y guarda si no existe
    public MentorProfiles getOrCreateAndSaveIfNeeded(Users mentorUser) {
        return repository.findByMentor_Id(mentorUser.getId())
                .orElseGet(() -> {
                    MentorProfiles nuevo = new MentorProfiles();
                    nuevo.setMentor(mentorUser);
                    nuevo.setBiography("");
                    nuevo.setSkills("");
                    nuevo.setAvailability("");
                    nuevo.setRating(0.0);
                    return repository.save(nuevo);
                });
    }

    public MentorProfiles saveProfileForMentor(Users mentorUser, MentorProfiles profile) {
        profile.setMentor(mentorUser);
        return repository.save(profile);
    }
}


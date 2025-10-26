package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.MentorProfiles;
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
}


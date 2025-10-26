package com.matchMenthor.matchMenthor.Servicio;

import com.matchMenthor.matchMenthor.Modelo.Matches;
import com.matchMenthor.matchMenthor.Repositorio.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchesService {

    private final MatchRepository repository;

    public MatchesService(MatchRepository repository) {
        this.repository = repository;
    }

    public List<Matches> getAll() {
        return repository.findAll();
    }

    public Optional<Matches> getById(Long id) {
        return repository.findById(id);
    }

    public Matches create(Matches match) {
        return repository.save(match);
    }

    public Matches update(Long id, Matches newMatch) {
        return repository.findById(id)
                .map(match -> {
                    match.setScore(newMatch.getScore());
                    match.setStatus(newMatch.getStatus());
                    return repository.save(match);
                })
                .orElseThrow(() -> new RuntimeException("Match no encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

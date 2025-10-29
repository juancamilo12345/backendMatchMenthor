package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.MentorInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorInfoRepository extends JpaRepository<MentorInfo, Long> {
    Optional<MentorInfo> findByUserId(Long userId);
}

package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Long> {
    Optional<StudentInfo> findByUserId(Long userId);
}

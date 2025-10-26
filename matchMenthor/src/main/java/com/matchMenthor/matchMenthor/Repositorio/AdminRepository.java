package com.matchMenthor.matchMenthor.Repositorio;

import com.matchMenthor.matchMenthor.Modelo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
}


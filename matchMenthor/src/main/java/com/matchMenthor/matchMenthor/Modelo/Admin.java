package com.matchMenthor.matchMenthor.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Ejemplo: si quieres registrar qué admin hizo cambios
    @Column(name = "last_action")
    private String lastAction;
}


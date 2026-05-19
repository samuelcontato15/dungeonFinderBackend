package com.pi4.dungeonFinderBackend.domain.entities;

import com.pi4.dungeonFinderBackend.domain.entities.CategoriaJogo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jogo")
@Getter
@Setter
public class Jogo {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NOME", length = 30, nullable = false)
    private String nome;

    @Column(name = "SLUG", length = 30, nullable = false)
    private String slug;

    @Column(name = "CAPA", columnDefinition = "TEXT", nullable = false)
    private String capa;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime criadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORIA", length = 20, nullable = false)
    private CategoriaJogo categoria;
}
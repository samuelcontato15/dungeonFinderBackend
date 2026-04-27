package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "membro_guilda")
@Getter
@Setter
public class MembroGuilda {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "GUILDA_ID", nullable = false)
    private Guilda guilda;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAPEL", nullable = false)
    private PapelGuilda papel;

    @Column(name = "ENTROU_EM", nullable = false)
    private LocalDateTime entrouEm;

}
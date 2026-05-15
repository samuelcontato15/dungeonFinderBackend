package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "guilda")
@Getter
@Setter
public class Guilda {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "BANNER", columnDefinition = "TEXT")
    private String banner;

    @Column(name = "MAX_MEMBROS", nullable = false)
    private Integer maxMembros;

    @ManyToOne
    @JoinColumn(name = "CRIADO_POR_ID", nullable = false)
    private Usuario criadoPor;

    @ManyToOne
    @JoinColumn(name = "JOGO_ID", nullable = false)
    private Jogo jogo;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime criadoEm;

}
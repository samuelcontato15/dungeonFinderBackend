package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "raid")
@Getter
@Setter
public class Raid {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "JOGO_ID", nullable = false)
    private Jogo jogo;

    @ManyToOne
    @JoinColumn(name = "GUILDA_ID", nullable = false)
    private Guilda guilda;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "BANNER", columnDefinition = "TEXT")
    private String banner;

    @Column(name = "MIN_JOGADORES", nullable = false)
    private Integer minJogadores;

    @Column(name = "MAX_JOGADORES", nullable = false)
    private Integer maxJogadores;

    @Column(name = "INICIO_EM", nullable = false)
    private LocalDateTime inicioEm;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime criadoEm;

}
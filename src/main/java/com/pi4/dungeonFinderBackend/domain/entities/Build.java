package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "build")
@Getter
@Setter
public class Build {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "JOGO_ID", nullable = false)
    private Jogo jogo;

    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @Column(name = "CLASSE", nullable = false)
    private String classe;

    @Enumerated(EnumType.STRING)
    @Column(name = "FUNCAO", nullable = false)
    private FuncaoBuild funcao;

    @Column(name = "DESCRICAO", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "CONTEUDO", columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "PUBLICA", nullable = false)
    private Boolean publica;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime criadoEm;

    public enum FuncaoBuild {
    }

}
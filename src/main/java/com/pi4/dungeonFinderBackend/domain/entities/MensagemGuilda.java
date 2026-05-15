package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensagem_guilda")
@Getter
@Setter
public class MensagemGuilda {

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

    @Column(name = "CONTEUDO", columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column(name = "ENVIADO_EM", nullable = false)
    private LocalDateTime enviadoEm;

}
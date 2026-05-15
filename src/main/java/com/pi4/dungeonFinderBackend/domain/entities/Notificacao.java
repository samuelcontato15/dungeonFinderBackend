package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacao")
@Getter
@Setter
public class Notificacao {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false)
    private TipoNotificacao tipo;

    @Column(name = "MENSAGEM", columnDefinition = "TEXT", nullable = false)
    private String mensagem;

    @Column(name = "LIDA", nullable = false)
    private Boolean lida;

    @Column(name = "REFERENCIA_ID")
    private UUID referenciaId;

    @Column(name = "REFERENCIA_TIPO")
    private String referenciaTipo;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime criadoEm;

}
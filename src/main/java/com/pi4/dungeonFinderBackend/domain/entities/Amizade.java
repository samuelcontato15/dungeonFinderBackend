package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "amizade")
@Getter
@Setter
public class Amizade {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "SOLICITANTE_ID", nullable = false)
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "DESTINATARIO_ID", nullable = false)
    private Usuario destinatario;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private StatusAmizade status;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime criadoEm;

}
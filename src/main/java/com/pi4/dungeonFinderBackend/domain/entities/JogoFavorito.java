package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "jogo_favorito")
@Getter
@Setter
public class JogoFavorito {

    @EmbeddedId
    private JogoFavoritoId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @MapsId("jogoId")
    @JoinColumn(name = "JOGO_ID", nullable = false)
    private Jogo jogo;

    @Embeddable
    @Getter
    @Setter
    public static class JogoFavoritoId implements Serializable {

        @Column(name = "USUARIO_ID")
        private UUID usuarioId;

        @Column(name = "JOGO_ID")
        private UUID jogoId;

    }

}
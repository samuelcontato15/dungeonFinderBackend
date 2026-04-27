package com.pi4.dungeonFinderBackend.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "participante_raid")
@Getter
@Setter
public class ParticipanteRaid {

    @EmbeddedId
    private ParticipanteRaidId id;

    @ManyToOne
    @MapsId("raidId")
    @JoinColumn(name = "RAID_ID", nullable = false)
    private Raid raid;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @Column(name = "INSCRITO_EM", nullable = false)
    private LocalDateTime inscritoEm;

    @Embeddable
    @Getter
    @Setter
    public static class ParticipanteRaidId implements Serializable {

        @Column(name = "RAID_ID")
        private UUID raidId;

        @Column(name = "USUARIO_ID")
        private UUID usuarioId;

    }

}
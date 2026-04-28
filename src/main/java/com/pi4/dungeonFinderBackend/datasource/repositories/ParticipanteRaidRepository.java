package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.ParticipanteRaid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipanteRaidRepository extends JpaRepository<ParticipanteRaid, ParticipanteRaid.ParticipanteRaidId> {
    List<ParticipanteRaid> findByRaidId(UUID raidId);
    boolean existsByRaidIdAndUsuarioId(UUID raidId, UUID usuarioId);
}
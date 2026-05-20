package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.ParticipanteRaid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipanteRaidRepository extends JpaRepository<ParticipanteRaid, ParticipanteRaid.ParticipanteRaidId> {

    @Query("SELECT pr FROM ParticipanteRaid pr JOIN FETCH pr.usuario WHERE pr.id.raidId = :raidId")
    List<ParticipanteRaid> findByRaidIdWithUsuario(@Param("raidId") UUID raidId);

    boolean existsByRaidIdAndUsuarioId(UUID raidId, UUID usuarioId);

    // Conta quantos participantes estão inscritos em uma raid
    long countByRaidId(UUID raidId);

    void deleteByRaidId(UUID raidId);
}
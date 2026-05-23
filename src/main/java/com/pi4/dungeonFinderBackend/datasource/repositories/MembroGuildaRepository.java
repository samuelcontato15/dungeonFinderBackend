package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.MembroGuilda;
import com.pi4.dungeonFinderBackend.domain.entities.PapelGuilda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembroGuildaRepository extends JpaRepository<MembroGuilda, UUID> {
    Optional<MembroGuilda> findByGuildaIdAndUsuarioId(UUID guildaId, UUID usuarioId);
    List<MembroGuilda> findByGuildaId(UUID guildaId);
    boolean existsByGuildaIdAndUsuarioId(UUID guildaId, UUID usuarioId);
    List<MembroGuilda> findByUsuarioId(UUID usuarioId);
}
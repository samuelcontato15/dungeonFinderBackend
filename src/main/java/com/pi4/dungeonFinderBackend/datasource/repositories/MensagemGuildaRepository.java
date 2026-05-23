package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.MensagemGuilda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensagemGuildaRepository extends JpaRepository<MensagemGuilda, UUID> {
    List<MensagemGuilda> findByGuildaIdOrderByEnviadoEmAsc(UUID guildaId);
    List<MensagemGuilda> findByGuildaId(UUID guildaId);
}
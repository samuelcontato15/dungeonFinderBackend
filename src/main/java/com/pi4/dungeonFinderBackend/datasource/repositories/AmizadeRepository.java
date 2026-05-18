package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.Amizade;
import com.pi4.dungeonFinderBackend.domain.entities.StatusAmizade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AmizadeRepository extends JpaRepository<Amizade, UUID> {

    List<Amizade> findByStatusAndSolicitanteIdOrStatusAndDestinatarioId(
            StatusAmizade status1, UUID solicitanteId,
            StatusAmizade status2, UUID destinatarioId
    );

    boolean existsBySolicitanteIdAndDestinatarioIdOrSolicitanteIdAndDestinatarioId(
            UUID solicitanteId1, UUID destinatarioId1,
            UUID solicitanteId2, UUID destinatarioId2
    );

    List<Amizade> findBySolicitanteIdOrDestinatarioId(UUID usuarioId, UUID usuarioId1);
    List<Amizade> findByStatusAndDestinatarioId(
            StatusAmizade status,
            UUID destinatarioId
    );
    @Query("SELECT a FROM Amizade a JOIN FETCH a.solicitante JOIN FETCH a.destinatario WHERE a.id = :id")
    Optional<Amizade> findByIdWithUsuarios(@Param("id") UUID id);
}
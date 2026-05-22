package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
    List<Notificacao> findByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);
    long countByUsuarioIdAndLidaFalse(UUID usuarioId);
    List<Notificacao> findByUsuarioIdAndLida(UUID usuarioId, Boolean lida);

    List<Notificacao> findByUsuarioIdAndLidaFalseOrderByCriadoEmDesc(UUID usuarioId);
}
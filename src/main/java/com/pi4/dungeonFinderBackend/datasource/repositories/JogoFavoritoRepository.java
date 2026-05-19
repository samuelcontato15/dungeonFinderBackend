package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.JogoFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JogoFavoritoRepository extends JpaRepository<JogoFavorito, JogoFavorito.JogoFavoritoId> {
    @Query("SELECT jf FROM JogoFavorito jf WHERE jf.id.usuarioId = :usuarioId")
    List<JogoFavorito> findByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT COUNT(jf) > 0 FROM JogoFavorito jf WHERE jf.id.usuarioId = :usuarioId AND jf.id.jogoId = :jogoId")
    boolean existsByUsuarioIdAndJogoId(@Param("usuarioId") UUID usuarioId, @Param("jogoId") UUID jogoId);
}
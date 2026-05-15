package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.Build;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuildRepository extends JpaRepository<Build, UUID> {
    List<Build> findByUsuarioId(UUID usuarioId);
}
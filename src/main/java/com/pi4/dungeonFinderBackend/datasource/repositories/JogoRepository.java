package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, UUID> {
    boolean existsBySlug(String slug);
    boolean existsByNome(String nome);
}
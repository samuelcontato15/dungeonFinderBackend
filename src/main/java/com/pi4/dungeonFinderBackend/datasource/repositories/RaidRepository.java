package com.pi4.dungeonFinderBackend.datasource.repositories;

import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import com.pi4.dungeonFinderBackend.domain.entities.JogoFavorito;
import com.pi4.dungeonFinderBackend.domain.entities.Raid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RaidRepository extends JpaRepository<Raid, UUID> {
    boolean existsByJogo(Jogo jogo);
    boolean existsByNome(String nome);
}
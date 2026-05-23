package com.pi4.dungeonFinderBackend.config;

import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import com.pi4.dungeonFinderBackend.domain.entities.CategoriaJogo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GameInitializer implements CommandLineRunner {

    private final JogoRepository jogoRepository;

    @Override
    public void run(String... args) {
        if (jogoRepository.count() > 0) {
            System.out.println("Jogos já existentes no banco. Nenhum jogo foi inserido.");
            return;
        }

        Jogo[] jogos = {
                criarJogo("World of Warcraft", "world-of-warcraft", CategoriaJogo.MMO),
                criarJogo("Final Fantasy XIV", "final-fantasy-xiv", CategoriaJogo.MMO),
                criarJogo("Albion Online", "albion-online", CategoriaJogo.MMO),
                criarJogo("The Elder Scrolls Online", "the-elder-scrolls-online", CategoriaJogo.MMO),
                criarJogo("Baldur's Gate 3", "baldurs-gate-3", CategoriaJogo.RPG),
                criarJogo("Dungeons & Dragons", "dungeons-dragons", CategoriaJogo.RPG),
                criarJogo("Tormenta", "tormenta", CategoriaJogo.RPG),
                criarJogo("Ordem Paranormal", "ordem-paranormal", CategoriaJogo.RPG),
                criarJogo("Daggerheart", "daggerheart", CategoriaJogo.RPG),
                criarJogo("Elden Ring", "elden-ring", CategoriaJogo.MMO)  // Elden Ring pode ser considerado MMO? Ou RPG? Vou deixar como MMO
        };

        for (Jogo jogo : jogos) {
            jogoRepository.save(jogo);
        }

        System.out.println(jogos.length + " jogos inseridos com sucesso!");
    }

    private Jogo criarJogo(String nome, String slug, CategoriaJogo categoria) {
        Jogo jogo = new Jogo();
        jogo.setNome(nome);
        jogo.setSlug(slug);
        jogo.setCapa("");
        jogo.setCriadoEm(LocalDateTime.now());
        jogo.setCategoria(categoria);
        return jogo;
    }
}
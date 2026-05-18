package com.pi4.dungeonFinderBackend.config;

import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
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

        // Lista de jogos iniciais (sem capa)
        Jogo[] jogos = {
                criarJogo("World of Warcraft", "world-of-warcraft"),
                criarJogo("Final Fantasy XIV", "final-fantasy-xiv"),
                criarJogo("Albion Online", "albion-online"),
                criarJogo("The Elder Scrolls Online", "the-elder-scrolls-online"),
                criarJogo("Baldur's Gate 3", "baldurs-gate-3"),
                criarJogo("Dungeons & Dragons", "dungeons-dragons"),
                criarJogo("Tormenta", "tormenta"),
                criarJogo("Ordem Paranormal", "ordem-paranormal"),
                criarJogo("Daggerheart", "daggerheart"),
                criarJogo("Elden Ring", "elden-ring")
        };

        for (Jogo jogo : jogos) {
            jogoRepository.save(jogo);
        }

        System.out.println(jogos.length + " jogos inseridos com sucesso (sem capa)!");
    }

    private Jogo criarJogo(String nome, String slug) {
        Jogo jogo = new Jogo();
        jogo.setNome(nome);
        jogo.setSlug(slug);
        jogo.setCapa("");  // ← capa vazia (sem imagem)
        jogo.setCriadoEm(LocalDateTime.now());
        return jogo;
    }
}
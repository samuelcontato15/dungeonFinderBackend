package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JogoService {

    private final JogoRepository jogoRepository;

    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    public Jogo buscarPorId(UUID id) {
        return jogoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));
    }

    public Jogo criar(Jogo jogo) {
        if (jogoRepository.existsByNome(jogo.getNome()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome já cadastrado");

        if (jogoRepository.existsBySlug(jogo.getSlug()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slug já cadastrado");

        jogo.setCriadoEm(LocalDateTime.now());
        return jogoRepository.save(jogo);
    }

    public Jogo atualizar(UUID id, Jogo dadosNovos) {
        Jogo jogo = buscarPorId(id);

        jogo.setNome(dadosNovos.getNome());
        jogo.setSlug(dadosNovos.getSlug());
        jogo.setCapa(dadosNovos.getCapa());

        return jogoRepository.save(jogo);
    }

    public void deletar(UUID id) {
        buscarPorId(id);
        jogoRepository.deleteById(id);
    }
}
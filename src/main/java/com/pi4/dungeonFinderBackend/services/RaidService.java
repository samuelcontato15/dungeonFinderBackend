package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.RaidRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import com.pi4.dungeonFinderBackend.domain.entities.Raid;
import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RaidService {

    private final RaidRepository raidRepository;
    private final JogoRepository jogoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Raid> listarTodos() {
        return raidRepository.findAll();
    }

    public List<Raid> listarPorJogo(UUID jogoId) {
        return raidRepository.findByJogoId(jogoId);
    }

    public Raid buscarPorId(UUID id) {
        return raidRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Raid não encontrada"));
    }

    public Raid criar(Raid raid, UUID jogoId, UUID usuarioId) {
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        raid.setJogo(jogo);
        raid.setCriadoPor(usuario);
        raid.setCriadoEm(LocalDateTime.now());
        return raidRepository.save(raid);
    }

    public void deletar(UUID id, UUID usuarioId) {
        Raid raid = buscarPorId(id);

        if (!raid.getCriadoPor().getId().equals(usuarioId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode deletar esta raid");

        raidRepository.deleteById(id);
    }
}
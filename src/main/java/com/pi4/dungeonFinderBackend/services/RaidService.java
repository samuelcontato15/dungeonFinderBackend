package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.RaidRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Raid;
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

    public List<Raid> listarTodos(UUID id) {
        return raidRepository.findAll();
    }

    public Raid buscarPorId(UUID id) {
        return raidRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Raid não encontrada"));
    }

    public Raid criar(Raid raid, UUID id) {

        if (raidRepository.existsByNome(raid.getNome()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome já cadastrado");

        raid.setCriadoEm(LocalDateTime.now());
        return raidRepository.save(raid);
    }

    public Raid atualizar(UUID id, Raid dadosNovos) {

        Raid raid = buscarPorId(id);
        raid.setNome(dadosNovos.getNome());
        raid.setDescricao(dadosNovos.getDescricao());

        return raidRepository.save(raid);
    }

    public void deletar(UUID id) {
        buscarPorId(id);
        raidRepository.deleteById(id);
    }

}
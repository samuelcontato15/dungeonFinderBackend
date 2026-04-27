package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.BuildRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Build;
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
public class BuildService {

    private final BuildRepository buildRepository;

    public List<Build> listarTodos() {
        return buildRepository.findAll();
    }

    public Build buscarPorId(UUID id) {
        return buildRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Build não encontrada"));
    }

    public Build criar(Build build) {
        if (buildRepository.existsByNome(build.getTitulo()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Titulo já cadastrado");

        build.setCriadoEm(LocalDateTime.now());
        return buildRepository.save(build);
    }

    public Build atualizar(UUID id, Build dadosNovos) {
        Build build = buscarPorId(id);

        build.setTitulo(dadosNovos.getTitulo());
        build.setClasse(dadosNovos.getClasse());
        build.setFuncao(dadosNovos.getFuncao());

        return buildRepository.save(build);
    }

    public void deletar(UUID id) {
        buscarPorId(id);
        buildRepository.deleteById(id);
    }
}
package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.BuildRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public List<Build> listarTodos() {
        return buildRepository.findAll();
    }

    public List<Build> listarPorUsuario(UUID usuarioId) {
        return buildRepository.findByUsuarioId(usuarioId);
    }

    public Build buscarPorId(UUID id) {
        return buildRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Build não encontrada"));
    }

    public Build criar(Build build, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        build.setUsuario(usuario);
        build.setCriadoEm(LocalDateTime.now());
        return buildRepository.save(build);
    }

    public Build atualizar(UUID id, Build dadosNovos, UUID usuarioId) {
        Build build = buscarPorId(id);
        verificarDono(build, usuarioId);

        build.setTitulo(dadosNovos.getTitulo());
        build.setClasse(dadosNovos.getClasse());
        build.setFuncao(dadosNovos.getFuncao());
        build.setDescricao(dadosNovos.getDescricao());
        build.setConteudo(dadosNovos.getConteudo());
        build.setPublica(dadosNovos.getPublica());

        return buildRepository.save(build);
    }

    public void deletar(UUID id, UUID usuarioId) {
        Build build = buscarPorId(id);
        verificarDono(build, usuarioId);
        buildRepository.deleteById(id);
    }

    private void verificarDono(Build build, UUID usuarioId) {
        if (!build.getUsuario().getId().equals(usuarioId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para isso");
    }
}
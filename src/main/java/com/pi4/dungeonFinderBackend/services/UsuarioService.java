package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
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
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public Usuario criar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");

        if (usuarioRepository.existsByNick(usuario.getNick()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname já cadastrado");

        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setIsAdmin(false);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(UUID id, Usuario dadosNovos) {
        Usuario usuario = buscarPorId(id);

        usuario.setNick(dadosNovos.getNick());
        usuario.setFotoPerfil(dadosNovos.getFotoPerfil());
        usuario.setBio(dadosNovos.getBio());

        return usuarioRepository.save(usuario);
    }

    public void deletar(UUID id) {
        buscarPorId(id);
        usuarioRepository.deleteById(id);
    }

    public Usuario tornarAdmin(UUID id) {

        Usuario usuario = buscarPorId(id);

        usuario.setIsAdmin(true);

        return usuarioRepository.save(usuario);
    }
}




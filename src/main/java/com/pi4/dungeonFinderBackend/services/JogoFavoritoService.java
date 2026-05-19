package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.JogoFavoritoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import com.pi4.dungeonFinderBackend.domain.entities.JogoFavorito;
import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JogoFavoritoService {

    private final JogoFavoritoRepository jogoFavoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    public List<JogoFavorito> listarFavoritosDoUsuario(UUID usuarioId) {
        return jogoFavoritoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public JogoFavorito adicionar(UUID usuarioId, UUID jogoId) {
        if (jogoFavoritoRepository.existsByUsuarioIdAndJogoId(usuarioId, jogoId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Jogo já está nos favoritos");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));

        JogoFavorito.JogoFavoritoId id = new JogoFavorito.JogoFavoritoId();
        id.setUsuarioId(usuarioId);
        id.setJogoId(jogoId);

        JogoFavorito favorito = new JogoFavorito();
        favorito.setId(id);
        favorito.setUsuario(usuario);
        favorito.setJogo(jogo);

        return jogoFavoritoRepository.save(favorito);
    }

    public void remover(UUID usuarioId, UUID jogoId) {
        JogoFavorito.JogoFavoritoId id = new JogoFavorito.JogoFavoritoId();
        id.setUsuarioId(usuarioId);
        id.setJogoId(jogoId);

        if (!jogoFavoritoRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorito não encontrado");

        jogoFavoritoRepository.deleteById(id);
    }
}
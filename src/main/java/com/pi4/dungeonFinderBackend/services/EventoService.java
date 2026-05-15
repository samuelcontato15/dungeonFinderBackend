package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.EventoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Evento;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
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
public class EventoService {

    private final EventoRepository eventoRepository;
    private final JogoRepository jogoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarPorJogo(UUID jogoId) {
        return eventoRepository.findByJogoId(jogoId);
    }

    public Evento buscarPorId(UUID id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
    }

    public Evento criar(Evento evento, UUID jogoId, UUID usuarioId) {
        verificarAdmin(usuarioId);

        if (eventoRepository.existsByNome(evento.getNome()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome já cadastrado");

        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));

        evento.setJogo(jogo);
        evento.setCriadoEm(LocalDateTime.now());
        return eventoRepository.save(evento);
    }

    public Evento atualizar(UUID id, Evento dadosNovos, UUID usuarioId) {
        verificarAdmin(usuarioId);

        Evento evento = buscarPorId(id);
        evento.setNome(dadosNovos.getNome());
        evento.setDescricao(dadosNovos.getDescricao());
        evento.setBanner(dadosNovos.getBanner());
        evento.setInicioEm(dadosNovos.getInicioEm());
        evento.setFimEm(dadosNovos.getFimEm());

        return eventoRepository.save(evento);
    }

    public void deletar(UUID id, UUID usuarioId) {
        verificarAdmin(usuarioId);
        buscarPorId(id);
        eventoRepository.deleteById(id);
    }

    private void verificarAdmin(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (!usuario.getIsAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
    }
}
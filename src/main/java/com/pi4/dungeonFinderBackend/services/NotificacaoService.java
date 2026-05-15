package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.NotificacaoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Notificacao;
import com.pi4.dungeonFinderBackend.domain.entities.TipoNotificacao;
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
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Notificacao> listarPorUsuario(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public List<Notificacao> listarNaoLidas(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndLida(usuarioId, false);
    }

    public Notificacao marcarComoLida(UUID notificacaoId, UUID usuarioId) {
        Notificacao notificacao = buscarPorId(notificacaoId, usuarioId);
        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }

    public void deletar(UUID notificacaoId, UUID usuarioId) {
        buscarPorId(notificacaoId, usuarioId);
        notificacaoRepository.deleteById(notificacaoId);
    }

    public void criar(UUID usuarioId, TipoNotificacao tipo, String mensagem, UUID referenciaId, String referenciaTipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setTipo(tipo);
        notificacao.setMensagem(mensagem);
        notificacao.setLida(false);
        notificacao.setReferenciaId(referenciaId);
        notificacao.setReferenciaTipo(referenciaTipo);
        notificacao.setCriadoEm(LocalDateTime.now());

        notificacaoRepository.save(notificacao);
    }

    private Notificacao buscarPorId(UUID notificacaoId, UUID usuarioId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));

        if (!notificacao.getUsuario().getId().equals(usuarioId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");

        return notificacao;
    }
}
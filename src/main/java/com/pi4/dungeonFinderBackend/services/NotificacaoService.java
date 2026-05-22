package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.NotificacaoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Evento;
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

    // =========================
    // EVENTOS
    // =========================

    public void notificarSobreEvento(List<Usuario> usuarios, Evento evento) {

        List<Notificacao> notificacoes = usuarios.stream()
                .map(usuario -> {

                    Notificacao notificacao = new Notificacao();

                    notificacao.setUsuario(usuario);
                    notificacao.setTipo(TipoNotificacao.EVENTO);
                    notificacao.setMensagem(
                            "Novo evento em "
                                    + evento.getJogo().getNome()
                                    + ": "
                                    + evento.getNome()
                    );

                    notificacao.setLida(false);
                    notificacao.setReferenciaId(evento.getId());
                    notificacao.setReferenciaTipo("EVENTO");
                    notificacao.setCriadoEm(LocalDateTime.now());

                    return notificacao;
                })
                .toList();

        notificacaoRepository.saveAll(notificacoes);
    }

    // =========================
    // CRIAR
    // =========================

    public Notificacao criar(
            UUID usuarioId,
            TipoNotificacao tipo,
            String mensagem,
            UUID referenciaId,
            String referenciaTipo
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        ));

        Notificacao notificacao = new Notificacao();

        notificacao.setUsuario(usuario);
        notificacao.setTipo(tipo);
        notificacao.setMensagem(mensagem);
        notificacao.setReferenciaId(referenciaId);
        notificacao.setReferenciaTipo(referenciaTipo);
        notificacao.setCriadoEm(LocalDateTime.now());
        notificacao.setLida(false);

        return notificacaoRepository.save(notificacao);
    }

    // =========================
    // LISTAR TODAS
    // =========================

    public List<Notificacao> listarPorUsuario(UUID usuarioId) {

        return notificacaoRepository
                .findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    // =========================
    // LISTAR NÃO LIDAS
    // =========================

    public List<Notificacao> listarNaoLidas(UUID usuarioId) {

        return notificacaoRepository
                .findByUsuarioIdAndLidaFalseOrderByCriadoEmDesc(usuarioId);
    }

    // =========================
    // CONTAR NÃO LIDAS
    // =========================

    public long contarNaoLidas(UUID usuarioId) {

        return notificacaoRepository
                .countByUsuarioIdAndLidaFalse(usuarioId);
    }

    // =========================
    // MARCAR COMO LIDA
    // =========================

    public Notificacao marcarComoLida(
            UUID notificacaoId,
            UUID usuarioId
    ) {

        Notificacao notificacao = notificacaoRepository
                .findById(notificacaoId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Notificação não encontrada"
                        ));

        if (!notificacao.getUsuario().getId().equals(usuarioId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso negado"
            );
        }

        notificacao.setLida(true);

        return notificacaoRepository.save(notificacao);
    }

    // =========================
    // MARCAR TODAS COMO LIDAS
    // =========================

    public void marcarTodasComoLidas(UUID usuarioId) {

        List<Notificacao> notificacoes =
                notificacaoRepository
                        .findByUsuarioIdAndLidaFalseOrderByCriadoEmDesc(usuarioId);

        notificacoes.forEach(n -> n.setLida(true));

        notificacaoRepository.saveAll(notificacoes);
    }

    // =========================
    // DELETAR
    // =========================

    public void deletar(
            UUID notificacaoId,
            UUID usuarioId
    ) {

        Notificacao notificacao = notificacaoRepository
                .findById(notificacaoId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Notificação não encontrada"
                        ));

        if (!notificacao.getUsuario().getId().equals(usuarioId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso negado"
            );
        }

        notificacaoRepository.delete(notificacao);
    }
}
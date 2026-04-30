package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.*;
import com.pi4.dungeonFinderBackend.domain.entities.*;
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
    private final AmizadeRepository amizadeRepository;
    private final MembroGuildaRepository membroGuildaRepository;



    public List<Notificacao> listarPorUsuario(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public List<Notificacao> listarNaoLidas(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndLida(usuarioId, false);
    }

    public Notificacao marcarComoLida(UUID notificacaoId, UUID usuarioId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));

        if (!notificacao.getUsuario().getId().equals(usuarioId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");

        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }

    public void deletar(UUID notificacaoId, UUID usuarioId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));

        if (!notificacao.getUsuario().getId().equals(usuarioId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");

        notificacaoRepository.deleteById(notificacaoId);
    }


    public void notificarNovoEvento(Evento evento, List<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            criar(usuario, TipoNotificacao.NOVO_EVENTO,
                    "Novo evento em " + evento.getJogo().getNome() + ": " + evento.getNome(),
                    evento.getId(), "EVENTO");
        }
    }


    public void notificarBuildPublica(Build build) {
        List<Amizade> amizades = amizadeRepository
                .findBySolicitanteIdOrDestinatarioId(build.getUsuario().getId(), build.getUsuario().getId());

        amizades.stream()
                .filter(a -> a.getStatus() == StatusAmizade.ACEITO)
                .forEach(a -> {
                    Usuario amigo = a.getSolicitante().getId().equals(build.getUsuario().getId())
                            ? a.getDestinatario()
                            : a.getSolicitante();
                    criar(amigo, TipoNotificacao.BUILD_PUBLICA,
                            build.getUsuario().getNick() + " postou uma nova build: " + build.getTitulo(),
                            build.getId(), "BUILD");
                });
    }

    public void notificarNovaRaid(Raid raid) {
        List<MembroGuilda> membros = membroGuildaRepository.findByGuildaId(raid.getJogo().getId());
        for (MembroGuilda membro : membros) {
            criar(membro.getUsuario(), TipoNotificacao.NOVA_RAID,
                    "Nova raid criada: " + raid.getNome(),
                    raid.getId(), "RAID");
        }
    }


    public void notificarNovoParticipanteRaid(Raid raid, Usuario participante) {
        criar(raid.getCriadoPor(), TipoNotificacao.NOVO_PARTICIPANTE_RAID,
                participante.getNick() + " entrou na sua raid: " + raid.getNome(),
                raid.getId(), "RAID");
    }


    public void notificarSolicitacaoAmizade(Amizade amizade) {
        criar(amizade.getDestinatario(), TipoNotificacao.SOLICITACAO_AMIZADE,
                amizade.getSolicitante().getNick() + " te enviou uma solicitação de amizade",
                amizade.getId(), "AMIZADE");
    }


    public void notificarAmigoEntrouNaGuilda(MembroGuilda novoMembro) {
        UUID usuarioId = novoMembro.getUsuario().getId();
        UUID guildaId = novoMembro.getGuilda().getId();

        List<Amizade> amizades = amizadeRepository
                .findBySolicitanteIdOrDestinatarioId(usuarioId, usuarioId);

        amizades.stream()
                .filter(a -> a.getStatus() == StatusAmizade.ACEITO)
                .forEach(a -> {
                    Usuario amigo = a.getSolicitante().getId().equals(usuarioId)
                            ? a.getDestinatario()
                            : a.getSolicitante();

                    boolean amigoEstaNaGuilda = membroGuildaRepository
                            .existsByGuildaIdAndUsuarioId(guildaId, amigo.getId());

                    if (amigoEstaNaGuilda)
                        criar(amigo, TipoNotificacao.AMIGO_ENTROU_GUILDA,
                                novoMembro.getUsuario().getNick() + " entrou na guilda " + novoMembro.getGuilda().getNome(),
                                guildaId, "GUILDA");
                });
    }



    private void criar(Usuario usuario, TipoNotificacao tipo, String mensagem, UUID referenciaId, String referenciaTipo) {
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
}
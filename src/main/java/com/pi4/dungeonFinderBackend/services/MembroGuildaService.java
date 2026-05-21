package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.AmizadeRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.GuildaRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.MembroGuildaRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembroGuildaService {

    private final MembroGuildaRepository membroGuildaRepository;
    private final GuildaRepository guildaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AmizadeRepository amizadeRepository;
    private final NotificacaoService notificacaoService;

    public List<MembroGuilda> listarMembros(UUID guildaId) {
        return membroGuildaRepository.findByGuildaId(guildaId);
    }

    public List<MembroGuilda> listarAmigosNaGuilda(UUID guildaId, UUID usuarioId) {
        List<Amizade> amizades = amizadeRepository.findBySolicitanteIdOrDestinatarioId(usuarioId, usuarioId);

        List<UUID> amigosIds = amizades.stream()
                .filter(a -> a.getStatus() == StatusAmizade.ACEITO)
                .map(a -> a.getSolicitante().getId().equals(usuarioId)
                        ? a.getDestinatario().getId()
                        : a.getSolicitante().getId())
                .collect(Collectors.toList());

        return membroGuildaRepository.findByGuildaId(guildaId).stream()
                .filter(m -> amigosIds.contains(m.getUsuario().getId()))
                .collect(Collectors.toList());
    }

    public MembroGuilda entrar(UUID guildaId, UUID usuarioId) {
        if (membroGuildaRepository.existsByGuildaIdAndUsuarioId(guildaId, usuarioId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já é membro desta guilda");

        Guilda guilda = guildaRepository.findById(guildaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guilda não encontrada"));

        long totalMembros = membroGuildaRepository.findByGuildaId(guildaId).size();
        if (totalMembros >= guilda.getMaxMembros())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Guilda está cheia");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        MembroGuilda membro = new MembroGuilda();
        membro.setGuilda(guilda);
        membro.setUsuario(usuario);
        membro.setPapel(PapelGuilda.MEMBRO);
        membro.setEntrouEm(LocalDateTime.now());

        return membroGuildaRepository.save(membro);
    }

    // Admin convida um usuário para a guilda → envia notificação CONVITE_GUILDA para o usuário
    public void convidar(UUID guildaId, UUID destinatarioId, UUID adminId) {
        verificarPermissaoEdicao(guildaId, adminId);

        Guilda guilda = guildaRepository.findById(guildaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guilda não encontrada"));

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (membroGuildaRepository.existsByGuildaIdAndUsuarioId(guildaId, destinatarioId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já é membro desta guilda");

        notificacaoService.criar(
                destinatarioId,
                TipoNotificacao.CONVITE_GUILDA,
                admin.getNick() + " convidou você para a guilda: " + guilda.getNome(),
                guildaId,
                "GUILDA"
        );
    }

    // Usuário solicita entrada → envia notificação PEDIDO_ENTRAR_GUILDA para o líder
    public void solicitarEntrada(UUID guildaId, UUID usuarioId) {
        Guilda guilda = guildaRepository.findById(guildaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guilda não encontrada"));

        if (membroGuildaRepository.existsByGuildaIdAndUsuarioId(guildaId, usuarioId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já é membro desta guilda");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // Notifica o líder (criadoPor)
        notificacaoService.criar(
                guilda.getCriadoPor().getId(),
                TipoNotificacao.PEDIDO_ENTRAR_GUILDA,
                usuario.getNick() + " quer entrar na guilda: " + guilda.getNome(),
                usuarioId,       // referenciaId = ID do usuário que pediu
                guildaId.toString() // referenciaTipo = ID da guilda (string)
        );
    }

    // Líder aprova entrada do usuário após receber PEDIDO_ENTRAR_GUILDA
    public MembroGuilda aprovar(UUID guildaId, UUID usuarioId, UUID adminId) {
        verificarPermissaoEdicao(guildaId, adminId);
        return entrar(guildaId, usuarioId);
    }

    public MembroGuilda alterarPapel(UUID guildaId, UUID alvoId, PapelGuilda novoPapel, UUID usuarioId) {
        verificarPermissaoLider(guildaId, usuarioId);

        MembroGuilda alvo = membroGuildaRepository.findByGuildaIdAndUsuarioId(guildaId, alvoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membro não encontrado"));

        if (novoPapel == PapelGuilda.LIDER)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é possível transferir a liderança por aqui");

        alvo.setPapel(novoPapel);
        return membroGuildaRepository.save(alvo);
    }

    public void sair(UUID guildaId, UUID usuarioId) {
        MembroGuilda membro = membroGuildaRepository.findByGuildaIdAndUsuarioId(guildaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não é membro desta guilda"));

        if (membro.getPapel() == PapelGuilda.LIDER)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O líder não pode sair da guilda, apenas deletá-la");

        membroGuildaRepository.delete(membro);
    }

    public void expulsar(UUID guildaId, UUID alvoId, UUID usuarioId) {
        verificarPermissaoEdicao(guildaId, usuarioId);

        MembroGuilda alvo = membroGuildaRepository.findByGuildaIdAndUsuarioId(guildaId, alvoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membro não encontrado"));

        if (alvo.getPapel() == PapelGuilda.LIDER)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é possível expulsar o líder");

        membroGuildaRepository.delete(alvo);
    }

    private void verificarPermissaoLider(UUID guildaId, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuario.getIsAdmin()) return;

        MembroGuilda membro = membroGuildaRepository.findByGuildaIdAndUsuarioId(guildaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é membro desta guilda"));

        if (membro.getPapel() != PapelGuilda.LIDER)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o líder pode fazer isso");
    }

    private void verificarPermissaoEdicao(UUID guildaId, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuario.getIsAdmin()) return;

        MembroGuilda membro = membroGuildaRepository.findByGuildaIdAndUsuarioId(guildaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é membro desta guilda"));

        if (membro.getPapel() == PapelGuilda.MEMBRO)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas líder ou oficial podem fazer isso");
    }
}
package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.GuildaRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.MembroGuildaRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.MensagemGuildaRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
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
public class MensagemGuildaService {

    private final MensagemGuildaRepository mensagemGuildaRepository;
    private final GuildaRepository guildaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembroGuildaRepository membroGuildaRepository;

    public List<MensagemGuilda> listarMensagens(UUID guildaId) {
        return mensagemGuildaRepository.findByGuildaIdOrderByEnviadoEmAsc(guildaId);
    }

    public MensagemGuilda enviar(UUID guildaId, String conteudo, UUID usuarioId) {
        if (conteudo == null || conteudo.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem não pode ser vazia");

        if (conteudo.length() > 300)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem não pode ter mais de 300 caracteres");

        Guilda guilda = guildaRepository.findById(guildaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guilda não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (!membroGuildaRepository.existsByGuildaIdAndUsuarioId(guildaId, usuarioId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é membro desta guilda");

        MensagemGuilda mensagem = new MensagemGuilda();
        mensagem.setGuilda(guilda);
        mensagem.setUsuario(usuario);
        mensagem.setConteudo(conteudo);
        mensagem.setEnviadoEm(LocalDateTime.now());

        return mensagemGuildaRepository.save(mensagem);
    }

    public void deletar(UUID mensagemId, UUID usuarioId) {
        MensagemGuilda mensagem = mensagemGuildaRepository.findById(mensagemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensagem não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        boolean isDono = mensagem.getUsuario().getId().equals(usuarioId);
        boolean isAdminGeral = usuario.getIsAdmin();

        MembroGuilda membro = membroGuildaRepository
                .findByGuildaIdAndUsuarioId(mensagem.getGuilda().getId(), usuarioId)
                .orElse(null);

        boolean isAdminGuilda = membro != null &&
                (membro.getPapel() == PapelGuilda.LIDER || membro.getPapel() == PapelGuilda.OFICIAL);

        if (!isDono && !isAdminGeral && !isAdminGuilda)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão para deletar esta mensagem");

        mensagemGuildaRepository.deleteById(mensagemId);
    }
}
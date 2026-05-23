package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.GuildaRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
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

@Service
@RequiredArgsConstructor
public class GuildaService {

    private final GuildaRepository guildaRepository;
    private final JogoRepository jogoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembroGuildaRepository membroGuildaRepository;

    public List<Guilda> listarTodas() {
        return guildaRepository.findAll();
    }

    public List<Guilda> listarPorJogo(UUID jogoId) {
        return guildaRepository.findByJogoId(jogoId);
    }

    public Guilda buscarPorId(UUID id) {
        return guildaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guilda não encontrada"));
    }

    public Guilda criar(Guilda guilda, UUID jogoId, UUID usuarioId) {
        if (guildaRepository.existsByNome(guilda.getNome()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome já cadastrado");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));

        guilda.setCriadoPor(usuario);
        guilda.setJogo(jogo);
        guilda.setCriadoEm(LocalDateTime.now());
        Guilda guildaSalva = guildaRepository.save(guilda);

        MembroGuilda lider = new MembroGuilda();
        lider.setGuilda(guildaSalva);
        lider.setUsuario(usuario);
        lider.setPapel(PapelGuilda.LIDER);
        lider.setEntrouEm(LocalDateTime.now());
        membroGuildaRepository.save(lider);
        return guildaRepository.save(guilda);
    }

    public Guilda atualizar(UUID id, Guilda dadosNovos, UUID usuarioId) {
        Guilda guilda = buscarPorId(id);
        verificarPermissaoEdicao(guilda.getId(), usuarioId);

        Jogo jogo = jogoRepository.findById(dadosNovos.getJogo().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));

        guilda.setNome(dadosNovos.getNome());
        guilda.setDescricao(dadosNovos.getDescricao());
        guilda.setBanner(dadosNovos.getBanner());
        guilda.setMaxMembros(dadosNovos.getMaxMembros());
        guilda.setJogo(jogo);

        return guildaRepository.save(guilda);
    }

    public void deletar(UUID id, UUID usuarioId) {
        Guilda guilda = buscarPorId(id);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        boolean isLider = guilda.getCriadoPor().getId().equals(usuarioId);
        boolean isAdminGeral = usuario.getIsAdmin();

        if (!isLider && !isAdminGeral)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o líder ou admin pode deletar a guilda");

        guildaRepository.deleteById(id);
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
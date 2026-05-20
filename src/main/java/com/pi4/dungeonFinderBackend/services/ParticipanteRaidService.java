package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.ParticipanteRaidRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.RaidRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.ParticipanteRaid;
import com.pi4.dungeonFinderBackend.domain.entities.Raid;
import com.pi4.dungeonFinderBackend.domain.entities.TipoNotificacao;
import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipanteRaidService {

    private final NotificacaoService notificacaoService;
    private final ParticipanteRaidRepository participanteRaidRepository;
    private final RaidRepository raidRepository;
    private final UsuarioRepository usuarioRepository;

    public List<ParticipanteRaid> listarPorRaid(UUID raidId) {
        return participanteRaidRepository.findByRaidIdWithUsuario(raidId);
    }

    @Transactional
    public ParticipanteRaid inscrever(UUID raidId, UUID usuarioId) {
        System.out.println("=== INSCREVENDO ===");
        System.out.println("raidId: " + raidId);
        System.out.println("usuarioId: " + usuarioId);

        // Verifica se já está inscrito
        if (participanteRaidRepository.existsByRaidIdAndUsuarioId(raidId, usuarioId)) {
            System.out.println("Usuário já inscrito");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já inscrito nesta raid");
        }

        // Busca a raid
        Raid raid = raidRepository.findById(raidId)
                .orElseThrow(() -> {
                    System.out.println("Raid não encontrada");
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Raid não encontrada");
                });

        // Verifica capacidade máxima
        long participantesAtuais = participanteRaidRepository.countByRaidId(raidId);
        System.out.println("Participantes atuais: " + participantesAtuais);
        System.out.println("Máximo permitido: " + raid.getMaxJogadores());

        if (participantesAtuais >= raid.getMaxJogadores()) {
            System.out.println("Raid lotada");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Raid já está lotada");
        }

        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    System.out.println("Usuário não encontrado");
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
                });

        // Cria a inscrição
        ParticipanteRaid.ParticipanteRaidId id = new ParticipanteRaid.ParticipanteRaidId();
        id.setRaidId(raidId);
        id.setUsuarioId(usuarioId);

        ParticipanteRaid participante = new ParticipanteRaid();
        participante.setId(id);
        participante.setRaid(raid);
        participante.setUsuario(usuario);
        participante.setInscritoEm(LocalDateTime.now());

        ParticipanteRaid salvo = participanteRaidRepository.save(participante);

        if (!raid.getCriadoPor().getId().equals(usuarioId)) {

            notificacaoService.criar(
                    raid.getCriadoPor().getId(),
                    TipoNotificacao.NOVO_PARTICIPANTE_RAID,
                    usuario.getNick() + " entrou na sua raid: " + raid.getNome(),
                    raid.getId(),
                    "RAID"
            );
        }
        System.out.println("Inscrição realizada com sucesso!");
        return salvo;
    }

    public void sair(UUID raidId, UUID usuarioId) {
        ParticipanteRaid.ParticipanteRaidId id = new ParticipanteRaid.ParticipanteRaidId();
        id.setRaidId(raidId);
        id.setUsuarioId(usuarioId);

        if (!participanteRaidRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não está inscrito nesta raid");

        participanteRaidRepository.deleteById(id);
    }

    @Transactional
    public void removerTodosDaRaid(UUID raidId) {
        participanteRaidRepository.deleteByRaidId(raidId);
    }
}
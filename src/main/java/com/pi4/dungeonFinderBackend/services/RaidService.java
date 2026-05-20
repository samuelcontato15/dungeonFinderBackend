package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.JogoRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.RaidRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import com.pi4.dungeonFinderBackend.domain.entities.Raid;
import com.pi4.dungeonFinderBackend.domain.entities.TipoNotificacao;
import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
import com.pi4.dungeonFinderBackend.dto.RaidRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RaidService {

    private final RaidRepository raidRepository;
    private final JogoRepository jogoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParticipanteRaidService participanteRaidService;
    private final NotificacaoService notificacaoService;

    public List<Raid> listarTodos() {
        return raidRepository.findAll();
    }

    public List<Raid> listarPorJogo(UUID jogoId) {
        return raidRepository.findByJogoId(jogoId);
    }

    public Raid buscarPorId(UUID id) {
        return raidRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Raid não encontrada"
                        )
                );
    }

    @Transactional
    public Raid criar(Raid raid, UUID jogoId, UUID usuarioId) {

        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Jogo não encontrado"
                        )
                );

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        )
                );

        if (raid.getMinJogadores() == null ||
                raid.getMinJogadores() < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O mínimo de jogadores deve ser pelo menos 1"
            );
        }

        if (raid.getMaxJogadores() == null ||
                raid.getMaxJogadores() > 10) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O máximo de jogadores não pode ultrapassar 10"
            );
        }

        if (raid.getMaxJogadores() <
                raid.getMinJogadores()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O máximo não pode ser menor que o mínimo"
            );
        }

        raid.setJogo(jogo);
        raid.setCriadoPor(usuario);
        raid.setCriadoEm(LocalDateTime.now());

        Raid salva = raidRepository.save(raid);

        notificacaoService.criar(
                usuarioId,
                TipoNotificacao.NOVA_RAID,
                "Sua raid '" + salva.getNome() + "' foi criada com sucesso!",
                salva.getId(),
                "RAID"
        );

        participanteRaidService.inscrever(
                salva.getId(),
                usuarioId
        );

        return salva;
    }

    @Transactional
    public void deletar(UUID id, UUID usuarioId) {

        Raid raid = buscarPorId(id);

        if (!raid.getCriadoPor().getId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas o criador pode deletar esta raid"
            );
        }

        participanteRaidService.removerTodosDaRaid(id);

        raidRepository.deleteById(id);
    }

    @Transactional
    public Raid editar(UUID id,
                       RaidRequest request,
                       UUID usuarioId) {

        Raid raid = buscarPorId(id);

        if (!raid.getCriadoPor().getId().equals(usuarioId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas o criador pode editar esta raid"
            );
        }

        if (request.minJogadores() < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Mínimo inválido"
            );
        }

        if (request.maxJogadores() > 10) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Máximo inválido"
            );
        }

        if (request.maxJogadores() <
                request.minJogadores()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Máximo menor que mínimo"
            );
        }

        raid.setNome(request.nome());
        raid.setDescricao(request.descricao());
        raid.setMinJogadores(request.minJogadores());
        raid.setMaxJogadores(request.maxJogadores());
        raid.setInicioEm(request.inicioEm());

        return raidRepository.save(raid);
    }
}
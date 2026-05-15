package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.AmizadeRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Amizade;
import com.pi4.dungeonFinderBackend.domain.entities.StatusAmizade;
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
public class AmizadeService {

    private final AmizadeRepository amizadeRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Amizade> listarAmizades(UUID usuarioId) {
        return amizadeRepository
                .findByStatusAndSolicitanteIdOrStatusAndDestinatarioId(StatusAmizade.ACEITO, usuarioId, StatusAmizade.ACEITO, usuarioId);
    }

    public List<Amizade> listarPendentes(UUID usuarioId) {
        return amizadeRepository.findByStatusAndDestinatarioId(
                StatusAmizade.PENDENTE,
                usuarioId
        );
    }
    public Amizade solicitar(UUID solicitanteId, UUID destinatarioId) {

        if (solicitanteId.equals(destinatarioId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        boolean existe = amizadeRepository
                .existsBySolicitanteIdAndDestinatarioIdOrSolicitanteIdAndDestinatarioId(solicitanteId, destinatarioId, destinatarioId, solicitanteId);

        if (existe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Usuario destinatario = usuarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Amizade amizade = new Amizade();
        amizade.setSolicitante(solicitante);
        amizade.setDestinatario(destinatario);
        amizade.setStatus(StatusAmizade.PENDENTE);
        amizade.setCriadoEm(LocalDateTime.now());

        return amizadeRepository.save(amizade);
    }
    public Amizade aceitar(UUID amizadeId, UUID destinatarioId) {
        Amizade amizade = amizadeRepository.findById(amizadeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!amizade.getDestinatario().getId().equals(destinatarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        amizade.setStatus(StatusAmizade.ACEITO);
        return amizadeRepository.save(amizade);
    }

    public void deletar(UUID amizadeId, UUID usuarioId) {

        Amizade amizade = amizadeRepository.findById(amizadeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!amizade.getSolicitante().getId().equals(usuarioId)
                && !amizade.getDestinatario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        amizadeRepository.delete(amizade);
    }
}
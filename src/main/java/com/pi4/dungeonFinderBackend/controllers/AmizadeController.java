package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.datasource.repositories.AmizadeRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Amizade;
import com.pi4.dungeonFinderBackend.domain.entities.StatusAmizade;
import com.pi4.dungeonFinderBackend.services.AmizadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/amizades")
@RequiredArgsConstructor
public class AmizadeController {

    private final AmizadeService amizadeService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Amizade>> listarAmizades(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(amizadeService.listarAmizades(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Amizade> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(amizadeService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}/pendentes")
    public ResponseEntity<List<Amizade>> listarPendentes(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(amizadeService.listarPendentes(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Amizade> solicitar(
            @RequestHeader("X-Usuario-Id") UUID solicitanteId,
            @RequestParam UUID destinatarioId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(amizadeService.solicitar(solicitanteId, destinatarioId));
    }

    @DeleteMapping("/{amizadeId}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID amizadeId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId
    ) {
        amizadeService.deletar(amizadeId, usuarioId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{amizadeId}/responder")
    public ResponseEntity<Amizade> responder(
            @PathVariable UUID amizadeId,
            @RequestParam StatusAmizade status,
            @RequestHeader("X-Usuario-Id") UUID usuarioId
    ) {
        return ResponseEntity.ok(amizadeService.responder(amizadeId, status, usuarioId));
    }
}
package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.Evento;
import com.pi4.dungeonFinderBackend.services.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @GetMapping("/jogo/{jogoId}")
    public ResponseEntity<List<Evento>> listarPorJogo(@PathVariable UUID jogoId) {
        return ResponseEntity.ok(eventoService.listarPorJogo(jogoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Evento> criar(
            @RequestBody Evento evento,
            @RequestParam UUID jogoId,
            @RequestParam UUID guildaId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.criar(evento, jogoId,   guildaId, usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(
            @PathVariable UUID id,
            @RequestBody Evento evento,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(eventoService.atualizar(id, evento, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        eventoService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
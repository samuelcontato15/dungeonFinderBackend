package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.Guilda;
import com.pi4.dungeonFinderBackend.services.GuildaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guildas")
@RequiredArgsConstructor
public class GuildaController {

    private final GuildaService guildaService;

    @GetMapping
    public ResponseEntity<List<Guilda>> listarTodas() {
        return ResponseEntity.ok(guildaService.listarTodas());
    }

    @GetMapping("/jogo/{jogoId}")
    public ResponseEntity<List<Guilda>> listarPorJogo(@PathVariable UUID jogoId) {
        return ResponseEntity.ok(guildaService.listarPorJogo(jogoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guilda> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(guildaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Guilda> criar(
            @RequestBody Guilda guilda,
            @RequestParam UUID jogoId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guildaService.criar(guilda, jogoId, usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guilda> atualizar(
            @PathVariable UUID id,
            @RequestBody Guilda guilda,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(guildaService.atualizar(id, guilda, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        guildaService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
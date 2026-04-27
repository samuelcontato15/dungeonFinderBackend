package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.Jogo;
import com.pi4.dungeonFinderBackend.services.JogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jogos")
@RequiredArgsConstructor
public class JogoController {

    private final JogoService jogoService;

    @GetMapping
    public ResponseEntity<List<Jogo>> listarTodos() {
        return ResponseEntity.ok(jogoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jogo> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(jogoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Jogo> criar(
            @RequestBody Jogo jogo,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jogoService.criar(jogo, usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jogo> atualizar(
            @PathVariable UUID id,
            @RequestBody Jogo jogo,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(jogoService.atualizar(id, jogo, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        jogoService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
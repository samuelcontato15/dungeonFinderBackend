package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.JogoFavorito;
import com.pi4.dungeonFinderBackend.services.JogoFavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jogos_favoritos")
@RequiredArgsConstructor
public class JogoFavoritoController {

    private final JogoFavoritoService jogoFavoritoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<JogoFavorito>> listarFavoritosDoUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(jogoFavoritoService.listarFavoritosDoUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<JogoFavorito> adicionar(
            @RequestHeader("X-Usuario-Id") UUID usuarioId,
            @RequestParam UUID jogoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jogoFavoritoService.adicionar(usuarioId, jogoId));
    }

    @DeleteMapping
    public ResponseEntity<Void> remover(
            @RequestHeader("X-Usuario-Id") UUID usuarioId,
            @RequestParam UUID jogoId) {
        jogoFavoritoService.remover(usuarioId, jogoId);
        return ResponseEntity.noContent().build();
    }
}
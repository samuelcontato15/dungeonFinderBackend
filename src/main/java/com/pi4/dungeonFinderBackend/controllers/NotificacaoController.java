package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.Notificacao;
import com.pi4.dungeonFinderBackend.services.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<Notificacao>> listarTodas(@RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(notificacaoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<List<Notificacao>> listarNaoLidas(@RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(notificacaoService.listarNaoLidas(usuarioId));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<Notificacao> marcarComoLida(
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        notificacaoService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
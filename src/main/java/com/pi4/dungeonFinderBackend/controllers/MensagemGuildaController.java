package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.MensagemGuilda;
import com.pi4.dungeonFinderBackend.services.MensagemGuildaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guildas/{guildaId}/mensagens")
@RequiredArgsConstructor
public class MensagemGuildaController {

    private final MensagemGuildaService mensagemGuildaService;

    @GetMapping
    public ResponseEntity<List<MensagemGuilda>> listarMensagens(@PathVariable UUID guildaId) {
        return ResponseEntity.ok(mensagemGuildaService.listarMensagens(guildaId));
    }

    @PostMapping
    public ResponseEntity<MensagemGuilda> enviar(
            @PathVariable UUID guildaId,
            @RequestParam String conteudo,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mensagemGuildaService.enviar(guildaId, conteudo, usuarioId));
    }

    @DeleteMapping("/{mensagemId}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID mensagemId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        mensagemGuildaService.deletar(mensagemId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
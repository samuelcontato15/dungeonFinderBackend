package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.MembroGuilda;
import com.pi4.dungeonFinderBackend.domain.entities.PapelGuilda;
import com.pi4.dungeonFinderBackend.services.MembroGuildaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guildas/{guildaId}/membros")
@RequiredArgsConstructor
public class MembroGuildaController {

    private final MembroGuildaService membroGuildaService;

    @GetMapping
    public ResponseEntity<List<MembroGuilda>> listarMembros(@PathVariable UUID guildaId) {
        return ResponseEntity.ok(membroGuildaService.listarMembros(guildaId));
    }

    @GetMapping("/amigos")
    public ResponseEntity<List<MembroGuilda>> listarAmigosNaGuilda(
            @PathVariable UUID guildaId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(membroGuildaService.listarAmigosNaGuilda(guildaId, usuarioId));
    }

    // Usuário entra direto na guilda (usado após aceitar convite via notificação)
    @PostMapping("/entrar")
    public ResponseEntity<MembroGuilda> entrar(
            @PathVariable UUID guildaId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membroGuildaService.entrar(guildaId, usuarioId));
    }

    // Admin/líder convida usuário → gera notificação CONVITE_GUILDA para o usuário
    @PostMapping("/{destinatarioId}/convidar")
    public ResponseEntity<Void> convidar(
            @PathVariable UUID guildaId,
            @PathVariable UUID destinatarioId,
            @RequestHeader("X-Usuario-Id") UUID adminId) {
        membroGuildaService.convidar(guildaId, destinatarioId, adminId);
        return ResponseEntity.ok().build();
    }

    // Usuário solicita entrada → gera notificação PEDIDO_ENTRAR_GUILDA para o líder
    @PostMapping("/solicitar-entrada")
    public ResponseEntity<Void> solicitarEntrada(
            @PathVariable UUID guildaId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        membroGuildaService.solicitarEntrada(guildaId, usuarioId);
        return ResponseEntity.ok().build();
    }

    // Líder aprova pedido de entrada (chamado ao aceitar PEDIDO_ENTRAR_GUILDA)
    @PostMapping("/{usuarioId}/aprovar")
    public ResponseEntity<MembroGuilda> aprovar(
            @PathVariable UUID guildaId,
            @PathVariable UUID usuarioId,
            @RequestHeader("X-Usuario-Id") UUID adminId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membroGuildaService.aprovar(guildaId, usuarioId, adminId));
    }

    @PatchMapping("/{alvoId}/papel")
    public ResponseEntity<MembroGuilda> alterarPapel(
            @PathVariable UUID guildaId,
            @PathVariable UUID alvoId,
            @RequestParam PapelGuilda papel,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(membroGuildaService.alterarPapel(guildaId, alvoId, papel, usuarioId));
    }

    @DeleteMapping("/sair")
    public ResponseEntity<Void> sair(
            @PathVariable UUID guildaId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        membroGuildaService.sair(guildaId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{alvoId}/expulsar")
    public ResponseEntity<Void> expulsar(
            @PathVariable UUID guildaId,
            @PathVariable UUID alvoId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        membroGuildaService.expulsar(guildaId, alvoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
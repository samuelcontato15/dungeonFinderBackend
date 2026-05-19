package com.pi4.dungeonFinderBackend.controllers;


import com.pi4.dungeonFinderBackend.domain.entities.Raid;
import com.pi4.dungeonFinderBackend.dto.RaidRequest;
import com.pi4.dungeonFinderBackend.services.RaidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/raids")
@RequiredArgsConstructor
public class RaidController {

    private final RaidService raidService;

    @GetMapping
    public ResponseEntity<List<Raid>> listarTodos() {
        return ResponseEntity.ok(raidService.listarTodos());
    }

    @GetMapping("/jogo/{jogoId}")
    public ResponseEntity<List<Raid>> listarPorJogo(@PathVariable UUID jogoId) {
        return ResponseEntity.ok(raidService.listarPorJogo(jogoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Raid> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(raidService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Raid> criar(
            @RequestBody RaidRequest request,
            @RequestParam UUID jogoId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        Raid raid = new Raid();
        raid.setNome(request.nome());
        raid.setDescricao(request.descricao());
        raid.setMinJogadores(request.minJogadores());
        raid.setMaxJogadores(request.maxJogadores());
        raid.setInicioEm(request.inicioEm());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(raidService.criar(raid, jogoId, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        raidService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
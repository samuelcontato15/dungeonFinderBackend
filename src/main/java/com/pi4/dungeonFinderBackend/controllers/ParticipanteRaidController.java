package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.ParticipanteRaid;
import com.pi4.dungeonFinderBackend.services.ParticipanteRaidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/participantes_raid")
@RequiredArgsConstructor
public class ParticipanteRaidController {

    private final ParticipanteRaidService participanteRaidService;

    @GetMapping("/raid/{raidId}")
    public ResponseEntity<List<ParticipanteRaid>> listarPorRaid(@PathVariable UUID raidId) {
        return ResponseEntity.ok(participanteRaidService.listarPorRaid(raidId));
    }

    @PostMapping
    public ResponseEntity<ParticipanteRaid> inscrever(
            @RequestParam UUID raidId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(participanteRaidService.inscrever(raidId, usuarioId));
    }

    @DeleteMapping
    public ResponseEntity<Void> sair(
            @RequestParam UUID raidId,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        participanteRaidService.sair(raidId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
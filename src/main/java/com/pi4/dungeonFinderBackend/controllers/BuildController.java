package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.domain.entities.Build;
import com.pi4.dungeonFinderBackend.services.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/builds")
@RequiredArgsConstructor
public class BuildController {

    private final BuildService buildService;

    @GetMapping
    public ResponseEntity<List<Build>> listarTodos() {
        return ResponseEntity.ok(buildService.listarTodos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Build>> listarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(buildService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Build> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(buildService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Build> criar(
            @RequestBody Build build,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(buildService.criar(build, usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Build> atualizar(
            @PathVariable UUID id,
            @RequestBody Build build,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        return ResponseEntity.ok(buildService.atualizar(id, build, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        buildService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
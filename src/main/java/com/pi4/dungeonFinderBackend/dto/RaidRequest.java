package com.pi4.dungeonFinderBackend.dto;

import java.time.LocalDateTime;

public record RaidRequest(
        String nome,
        String descricao,
        Integer minJogadores,
        Integer maxJogadores,
        LocalDateTime inicioEm
) {}
package com.pi4.dungeonFinderBackend.dto;

public record RegisterRequest(
        String email,
        String nick,
        String senha
) {}
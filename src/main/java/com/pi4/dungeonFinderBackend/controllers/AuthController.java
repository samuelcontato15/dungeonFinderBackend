package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
import com.pi4.dungeonFinderBackend.dto.LoginRequest;
import com.pi4.dungeonFinderBackend.dto.RegisterRequest;
import com.pi4.dungeonFinderBackend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        String token = jwtUtils.generateToken(request.email());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {
        if (usuarioRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }

        if (usuarioRepository.existsByNick(request.nick())) {
            return ResponseEntity.badRequest().body("Nick já cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(request.email());
        novoUsuario.setNick(request.nick());
        novoUsuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        novoUsuario.setIsAdmin(false);
        novoUsuario.setCriadoEm(LocalDateTime.now());

        usuarioRepository.save(novoUsuario);

        String token = jwtUtils.generateToken(request.email());
        return ResponseEntity.ok(token);
    }
}
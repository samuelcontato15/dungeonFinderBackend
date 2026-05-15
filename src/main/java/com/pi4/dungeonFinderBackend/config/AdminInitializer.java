package com.pi4.dungeonFinderBackend.config;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {

        if (usuarioRepository.existsByEmail("admin@admin.com")) {
            return;
        }

        Usuario admin = new Usuario();

        admin.setNick("admin");
        admin.setEmail("admin@admin.com");

        admin.setSenhaHash(
                new BCryptPasswordEncoder().encode("123456")
        );

        admin.setBio("Administrador");

        admin.setIsAdmin(true);

        admin.setCriadoEm(LocalDateTime.now());

        usuarioRepository.save(admin);

        System.out.println("ADMIN CRIADO");
    }
}
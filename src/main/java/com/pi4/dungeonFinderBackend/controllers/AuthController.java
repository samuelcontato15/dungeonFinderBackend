package com.pi4.dungeonFinderBackend.controllers;

import com.pi4.dungeonFinderBackend.dto.LoginRequest;
import com.pi4.dungeonFinderBackend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public String login(
            @RequestBody LoginRequest request
    ) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.email(),
                        request.senha()
                )
        );

        return jwtUtils.generateToken(
                request.email()
        );
    }
}
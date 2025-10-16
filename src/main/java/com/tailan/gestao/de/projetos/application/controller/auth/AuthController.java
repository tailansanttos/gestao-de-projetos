package com.tailan.gestao.de.projetos.application.controller.auth;

import com.tailan.gestao.de.projetos.application.dto.auth.LoginRequestDTO;
import com.tailan.gestao.de.projetos.application.dto.auth.LoginResponseDTO;
import com.tailan.gestao.de.projetos.application.dto.auth.RegisterRequestDTO;
import com.tailan.gestao.de.projetos.application.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }
}

package com.tailan.gestao.de.projetos.application.service.auth.impl;

import com.tailan.gestao.de.projetos.application.dto.auth.LoginRequestDTO;
import com.tailan.gestao.de.projetos.application.dto.auth.LoginResponseDTO;
import com.tailan.gestao.de.projetos.application.dto.auth.RegisterRequestDTO;
import com.tailan.gestao.de.projetos.application.dto.user.CreateUserDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UserResponseDTO;
import com.tailan.gestao.de.projetos.application.service.auth.AuthService;
import com.tailan.gestao.de.projetos.application.service.user.UserService;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.infrastructure.security.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService   jwtService;

    public AuthServiceImpl(UserService userService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
        //Valida as credenciais
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));

        // Define o usuário autenticado no contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.getUserByEmail(loginRequestDTO.email());
        String token = jwtService.generateToken(user);

        return new LoginResponseDTO(token, user.getEmail());
    }

    @Override
    public void register(RegisterRequestDTO registerRequestDTO) {
        CreateUserDTO createUserDTO = new CreateUserDTO(registerRequestDTO.name(), registerRequestDTO.email(), registerRequestDTO.password());
        UserResponseDTO user = userService.createUser(createUserDTO);
    }

    @Override
    public LoginResponseDTO refreshToken(LoginRequestDTO loginRequestDTO) {
        return null;
    }
}

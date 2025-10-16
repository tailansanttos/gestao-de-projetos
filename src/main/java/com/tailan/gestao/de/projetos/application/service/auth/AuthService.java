package com.tailan.gestao.de.projetos.application.service.auth;

import com.tailan.gestao.de.projetos.application.dto.auth.LoginRequestDTO;
import com.tailan.gestao.de.projetos.application.dto.auth.LoginResponseDTO;
import com.tailan.gestao.de.projetos.application.dto.auth.RegisterRequestDTO;

import javax.naming.AuthenticationException;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws AuthenticationException;
    void register(RegisterRequestDTO registerRequestDTO);
    LoginResponseDTO refreshToken(LoginRequestDTO loginRequestDTO);
}

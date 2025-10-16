package com.tailan.gestao.de.projetos.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tailan.gestao.de.projetos.core.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JWTService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withSubject(user.getEmail())
                    .withIssuedAt(creationDate())
                    .withExpiresAt(generateExpiration())
                    .withIssuer(issuer)
                    .withClaim("email", user.getEmail())
                    .sign(algorithm);
        }catch (JWTCreationException e){
            throw new JWTCreationException("Erro ao gerar token.", e);
        }
    }

    public String getSubjectFromToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            throw new JWTVerificationException("Token inválido ou expirado.", e);
        }
    }

    public boolean isTokenValid(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    private Instant creationDate(){
        return Instant.now();
    }
    public Instant generateExpiration(){
        return Instant.now().plusSeconds(3600);
    }
}

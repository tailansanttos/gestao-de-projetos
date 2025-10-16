package com.tailan.gestao.de.projetos.application.service.user.impl;

import com.tailan.gestao.de.projetos.application.dto.user.*;
import com.tailan.gestao.de.projetos.application.mapper.UserMapper;
import com.tailan.gestao.de.projetos.application.service.user.UserService;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.core.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder; // Agora podemos mockar
    }


    @Override
    public UserResponseDTO createUser(CreateUserDTO dto) {
        Optional<User> existingUser = userRepository.findByEmail(dto.email());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Usuário com esse email já cadastrado. Tente com outro email.");
        }

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(UUID userId, UpdateUserDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (dto.email() != null || !dto.email().isEmpty()) {
            Optional<User> existingUserWithEmail = userRepository.findByEmail(dto.email());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email já está sendo usado por outro usuário.");
            }
            user.setEmail(dto.email());
        }

        if (dto.name() != null || !dto.name().isEmpty()) {
            user.setName(dto.name());
        }

        User updateUser = userRepository.save(user);
        return userMapper.toResponse(updateUser);
    }

    @Override
    public void updatePassword(UUID userId, UpdatePasswordDTO dto) {
        //VERIFICAR SE PASSWORD ENCODER MATCHES SENHA ANTIGA COM A SENHA DO USER SALVA HASHEADA. VERIFICA SE A SENHA CRIPTOGRAFADA BATE COM A ANTIGA DO USUARIO
        User user = getUserById(userId);

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPasswordHash())){
            throw new IllegalArgumentException("Senha atual incorreta.");
        }
        user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
       userRepository.delete(user);
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    }

    @Override
    public List<UserResponseDTO> listAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public LoginResponseDTO login(LoginUserDTO dto) {
        User user = getByEmail(dto.email());
        String senhaLoginHash = passwordEncoder.encode(dto.password());
        if (!user.getPasswordHash().equals(senhaLoginHash)) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }

        return new LoginResponseDTO("token");
    }


    private User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Usuário não encontrado."));
    }
}

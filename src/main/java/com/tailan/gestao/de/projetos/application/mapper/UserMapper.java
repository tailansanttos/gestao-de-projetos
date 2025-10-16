package com.tailan.gestao.de.projetos.application.mapper;

import com.tailan.gestao.de.projetos.application.dto.user.CreateUserDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UserResponseDTO;
import com.tailan.gestao.de.projetos.core.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getId(),user.getName(), user.getEmail(), user.getCreatedAt());
    }

    public User toEntity(CreateUserDTO createUserDTO) {
        return new User(createUserDTO.name(), createUserDTO.email(), createUserDTO.password());
    }
}

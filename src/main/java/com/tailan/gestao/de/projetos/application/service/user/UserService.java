package com.tailan.gestao.de.projetos.application.service.user;

import com.tailan.gestao.de.projetos.application.dto.user.CreateUserDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UpdatePasswordDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UpdateUserDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UserResponseDTO;
import com.tailan.gestao.de.projetos.core.model.user.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(CreateUserDTO dto);
    UserResponseDTO updateUser(UUID userId, UpdateUserDTO dto);
    void updatePassword(UUID userId, UpdatePasswordDTO dto);
    void deleteUser(UUID userId);
    User getUserById(UUID userId);
    List<UserResponseDTO> listAllUsers();
}

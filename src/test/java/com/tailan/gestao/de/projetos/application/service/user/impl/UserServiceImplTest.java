package com.tailan.gestao.de.projetos.application.service.user.impl;

import com.tailan.gestao.de.projetos.application.dto.user.CreateUserDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UpdatePasswordDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UpdateUserDTO;
import com.tailan.gestao.de.projetos.application.dto.user.UserResponseDTO;
import com.tailan.gestao.de.projetos.application.mapper.UserMapper;
import com.tailan.gestao.de.projetos.core.model.user.User;
import com.tailan.gestao.de.projetos.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private CreateUserDTO createUserDTO;
    private User user;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createUserDTO = new CreateUserDTO("Tailan", "tailansanttos@gmail.com","1234");
        user = new User("Tailan", "tailansanttos@gmail.com","encodedPassword");
        user.setId(UUID.randomUUID());
        responseDTO = new UserResponseDTO(user.getId(),user.getName(),user.getEmail(),user.getCreatedAt());
    }

    @Test
    void mustCreateUserSuccessfully(){
        //Arrange (Preparando)
        when(userRepository.findByEmail(createUserDTO.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(createUserDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDTO);

        //Act (executa o metodo testado)
        UserResponseDTO result = userServiceImpl.createUser(createUserDTO);

        //Assert (verifica resultado)
        assertNotNull(result);
        assertEquals("Tailan", result.name());
        assertEquals("tailansanttos@gmail.com", result.email());

        verify(userRepository, times(1)).save(any(User.class)); //garantir que o save foi chamado uma vez
    }

    @Test
    void shouldThrowErrorWhenCreatingUserWithDuplicateEmail(){
        //Arrange
        when(userRepository.findByEmail(createUserDTO.email())).thenReturn(Optional.of(user));

        //Act e Assert
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> userServiceImpl.createUser(createUserDTO));

        assertEquals("Usuário com esse email já cadastrado. Tente com outro email.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class)); //garante que o save nunca foi chamado
    }
    @Test
    void mustUpdateUserDataSuccessfully(){
        //Arrange
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("Tailan", "tailan@gmail");
        responseDTO = new UserResponseDTO(user.getId(),user.getName(),updateUserDTO.email(),user.getCreatedAt());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateUserDTO.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDTO);

         //ACT
         UserResponseDTO result = userServiceImpl.updateUser(user.getId(), updateUserDTO);

         //Assert
         assertNotNull(result); assertEquals("tailan@gmail", result.email());
         verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void ShouldThrowAnExceptionIfUserDoesNotExist(){
        //Arrange
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("Tailan", "tailan@gmail");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> userServiceImpl.updateUser(user.getId(), updateUserDTO));

        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void mustThrowAnExceptionIfEmailIsDuplicate(){
        //Arrange
        User anotherUser = new User("Outro", "tailansanttos@gmail.com", "senha");
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("Tailan", "tailansanttos@gmail.com");
        anotherUser.setId(UUID.randomUUID());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateUserDTO.email())).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> userServiceImpl.updateUser(user.getId(), updateUserDTO));
        assertEquals("Email já está sendo usado por outro usuário.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void passwordMustBeUpdatedSuccessfully() {
        // Arrange
        UpdatePasswordDTO updatePassword = new UpdatePasswordDTO("oldPassword", "newPassword");
        user.setPasswordHash("oldPassword"); // não precisa ser hash real, só bate com o mock

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "oldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        userServiceImpl.updatePassword(user.getId(), updatePassword);

        // Assert
        assertEquals("encodedNewPassword", user.getPasswordHash());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowAnExceptionIfOldPasswordIncorrect(){
        UpdatePasswordDTO updatePassword = new UpdatePasswordDTO("incorrectPassword", "newPassword");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(updatePassword.oldPassword(),user.getPasswordHash())).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> userServiceImpl.updatePassword(user.getId(), updatePassword));
        assertEquals("Senha atual incorreta.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void mustDeleteUserSuccessfully(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(user.getId());

        userServiceImpl.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void shouldThrowExceptionIfUserDoesNotExistOnDelete(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> userServiceImpl.deleteUser(user.getId()));
        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(userRepository, never()).deleteById(user.getId());
    }

    @Test
    void mustListAllUsersSuccessfully(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt()));

        List<UserResponseDTO> list = userServiceImpl.listAllUsers();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("tailansanttos@gmail.com", list.get(0).email());

    }



}
package com.example.gymapp.service;


import com.example.gymapp.dto.auth.PasswordChangeRequestDTO;
import com.example.gymapp.exception.UnauthorizedException;
import com.example.gymapp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_ValidCredentials_NoExceptionThrown() {
        Long userId = 1L;
        String username = "testUser";
        String password = "testPassword";

        User user = mock(User.class);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(password);

        authenticationService.authenticate( username, password);

        verify(userService, times(1)).findByUsername(username);
    }


    @Test
    void authenticate_InvalidCredentials_UnauthorizedExceptionThrown() {
        Long userId = 1L;
        String username = "testUser";
        String password = "incorrectPassword";

        User user = new User();
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(NullPointerException.class, () -> authenticationService.authenticate(username, password));

        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void authenticate_UserNotFound_UnauthorizedExceptionThrown() {
        Long userId = 1L;
        String username = "nonExistentUser";
        String password = "password";

        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authenticationService.authenticate( username, password));

        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void updatePassword_UserFoundAndPasswordUpdated_NoExceptionThrown() {
        Long userId = 1L;

        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO();
        requestDTO.setUsername("testUser");
        requestDTO.setOldPassword("oldPassword");
        requestDTO.setNewPassword("newPassword");

        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("oldPassword");

        when(userService.findByUsername(requestDTO.getUsername())).thenReturn(Optional.of(mockUser));
        when(userService.updatePassword(anyString(), anyString())).thenReturn(true);

        authenticationService.updatePassword(requestDTO);

        verify(userService, times(1)).updatePassword(requestDTO.getUsername(), requestDTO.getNewPassword());
    }

    @Test
    void updatePassword_UserFoundAndPasswordNotUpdated_UnauthorizedExceptionThrown() {
        Long userId = 1L;
        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO();
        requestDTO.setUsername("testUser");
        requestDTO.setOldPassword("oldPassword");
        requestDTO.setNewPassword("newPassword");

        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("oldPassword");

        when(userService.findByUsername(requestDTO.getUsername())).thenReturn(Optional.of(mockUser));
        when(userService.updatePassword(anyString(), anyString())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> authenticationService.updatePassword(requestDTO));

        verify(userService, times(1)).findByUsername(requestDTO.getUsername());
        verify(userService, times(1)).updatePassword(requestDTO.getUsername(), requestDTO.getNewPassword());
    }

}
package com.example.gymapp.service;

import com.example.gymapp.dto.auth.PasswordChangeRequestDTO;
import com.example.gymapp.exception.ForbiddenException;
import com.example.gymapp.exception.UnauthorizedException;
import com.example.gymapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public final class AuthenticationService {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    public boolean authenticate(Long id, String username, String password) {
        checkPassword(username, password);
        checkAccess(id, username);
        return true;
    }

    public boolean updatePassword(Long id, PasswordChangeRequestDTO requestDTO) {
        if (authenticate(id, requestDTO.getUsername(), requestDTO.getOldPassword())) {
            return userService.updatePassword(requestDTO.getUsername(), requestDTO.getNewPassword());
        }
        return false;
    }

    private User findUserByUsername(String username) {
        return userService.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("No user found for authentication"));
    }

    private void checkPassword(String username, String password) {
        User user = findUserByUsername(username);
        if (!user.getPassword().equals(password)) {
            handleAuthenticationFailure(username);
        }
    }

    private void checkAccess(Long id, String username) {
        User user = findUserByUsername(username);
        if (!Objects.equals(user.getId(), id)) {
            handleAccessDenied(username);
        }
    }

    private void handleAuthenticationFailure(String username) {
        LOGGER.warn("{} — Bad login attempt with username {}", username);
        throw new UnauthorizedException("Bad login attempt");
    }

    private void handleAccessDenied(String username) {
        LOGGER.warn("{} — Attempt to perform an action with no access with username {}", username);
        throw new ForbiddenException("Access denied");
    }
}

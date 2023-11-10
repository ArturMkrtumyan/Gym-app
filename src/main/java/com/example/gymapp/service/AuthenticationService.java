package com.example.gymapp.service;

import com.example.gymapp.dto.auth.PasswordChangeRequestDTO;
import com.example.gymapp.exception.UnauthorizedException;
import com.example.gymapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;

    public void authenticate(Long id, String username, String password) {
        checkPassword(username, password);
    }

    public void updatePassword(Long id, PasswordChangeRequestDTO requestDTO) {
        authenticate(id, requestDTO.getUsername(), requestDTO.getOldPassword());

        if (!userService.updatePassword(requestDTO.getUsername(), requestDTO.getNewPassword())) {
            throw new NotFoundException("User not found");
        }
    }

    private void checkPassword(String username, String password) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UnauthorizedException("No user found for authentication"));
        if (!user.getPassword().equals(password)) {
            handleAuthenticationFailure(username);
        }
    }

    private void handleAuthenticationFailure(String username) {
        log.warn("{} — Bad login attempt", username);
        throw new UnauthorizedException("Bad login attempt");
    }
}
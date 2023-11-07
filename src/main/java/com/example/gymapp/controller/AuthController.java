package com.example.gymapp.controller;
import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.auth.PasswordChangeRequestDTO;
import com.example.gymapp.model.User;
import com.example.gymapp.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/auth", consumes = {"application/JSON"}, produces = {"application/JSON"})
public final class AuthController {
    private final AuthenticationService authService;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/{id}")
    @Operation(summary = "Authenticate user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials"),
                    @ApiResponse(responseCode = "403", description = "Access denied (wrong ID?)"),
                    @ApiResponse(responseCode = "422", description = "Username or password is null")})
    public ResponseEntity<?> login(@PathVariable("id") Long id, @RequestBody @Valid AuthDTO authDTO) {
        var user = modelMapper.map(authDTO, User.class);

        if (authService.authenticate(id, user.getUsername(), user.getPassword())) {
            LOGGER.info("User authentication succeeded for ID: {}", id);
            return ResponseEntity.ok().build();
        }

        LOGGER.info("User authentication failed for ID: {}", id);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Change user password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User changed password successfully"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials"),
                    @ApiResponse(responseCode = "403", description = "Access denied (wrong ID?)"),
                    @ApiResponse(responseCode = "404", description = "User with ID not found"),
                    @ApiResponse(responseCode = "422", description = "Username or password is null")})
    public ResponseEntity<?> changePassword(@PathVariable("id") Long id, @RequestBody @Valid PasswordChangeRequestDTO requestDTO) {
        boolean success = authService.updatePassword(id, requestDTO);

        if (success) {
            LOGGER.info("Password changed successfully for user with ID: {}", id);
            return ResponseEntity.ok().build();
        } else {
            LOGGER.info("User with ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }
}

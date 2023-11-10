package com.example.gymapp.controller;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.auth.PasswordChangeRequestDTO;
import com.example.gymapp.model.User;
import com.example.gymapp.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/auth", consumes = {"application/JSON"}, produces = {"application/JSON"})
public final class AuthController {
    private final AuthenticationService authService;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Authenticate user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials"),})
    public ResponseEntity<User> login(@PathVariable("id") Long id, @RequestBody @Valid AuthDTO authDTO) {
        var user = modelMapper.map(authDTO, User.class);

        authService.authenticate(id, user.getUsername(), user.getPassword());
        log.info("User authentication succeeded for ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Change user password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User changed password successfully"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials")})
    public ResponseEntity<AuthDTO> changePassword(@PathVariable("id") Long id, @RequestBody @Valid PasswordChangeRequestDTO requestDTO) {
        authService.updatePassword(id, requestDTO);
        log.info("Password changed successfully for user with ID: {}", id);
        return ResponseEntity.ok().build();
    }
}

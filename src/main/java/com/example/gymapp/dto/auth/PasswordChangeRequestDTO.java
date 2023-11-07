package com.example.gymapp.dto.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
@Getter
@Setter
public final class PasswordChangeRequestDTO {
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @NotBlank(message = "Password cannot be empty")
    private String oldPassword;
    @NotBlank(message = "New password cannot be empty")
    private String newPassword;
}

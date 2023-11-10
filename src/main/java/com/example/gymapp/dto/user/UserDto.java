package com.example.gymapp.dto.user;

import lombok.Data;


@Data
public class UserDto {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}

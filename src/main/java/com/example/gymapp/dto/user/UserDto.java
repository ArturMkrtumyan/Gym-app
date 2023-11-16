package com.example.gymapp.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}

package com.example.gymapp.dto.trainer;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public  class TrainerRequestDTO {
    @NotBlank(message = "First name cannot be empty")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
    @Min(value = 1)
    @NotBlank(message = "specialization cannot be empty")
    private Long specialization;
}
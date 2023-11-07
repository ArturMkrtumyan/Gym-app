package com.example.gymapp.dto.training;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public final class TrainingGetListRequestDTO {
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @NotBlank(message = "Password cannot be empty")
    private String password;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private String trainerName;
    private String traineeName;
    private Long trainingType;

}
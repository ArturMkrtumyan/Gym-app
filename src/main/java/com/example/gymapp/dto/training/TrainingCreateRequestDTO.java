package com.example.gymapp.dto.training;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public final class TrainingCreateRequestDTO {
    @NotBlank(message = "Trainee username cannot be empty")
    private String traineeUsername;
    @NotBlank(message = "Trainer username cannot be empty")
    private String trainerUsername;
    @NotBlank(message = "Training name cannot be empty")
    private String trainingName;
    @NotNull
    @FutureOrPresent(message = "Training date must be today or in the future")
    private LocalDate trainingDate;
    @Min(1)
    private int trainingDuration;
}
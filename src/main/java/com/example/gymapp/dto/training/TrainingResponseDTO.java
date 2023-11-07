package com.example.gymapp.dto.training;

import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TrainingResponseDTO {
    private String trainingName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    private TrainingType trainingType;
    private Integer trainingDuration;
    @JsonIgnoreProperties({"id", "username", "password", "trainingList", "trainees", "isActive", "specialization"})
    private Trainer trainer;

    @JsonIgnoreProperties({"id", "username", "password", "trainingList", "dateOfBirth", "address",
            "trainers", "isActive"})
    private Trainee trainee;
}

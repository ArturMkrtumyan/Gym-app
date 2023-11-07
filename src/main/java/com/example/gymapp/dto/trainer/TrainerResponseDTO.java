package com.example.gymapp.dto.trainer;

import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.TrainingType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public final class TrainerResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private Boolean isActive;
    @JsonIgnoreProperties({"id", "password", "trainingList", "trainers", "isActive", "dateOfBirth", "address"})
    private Set<Trainee> trainees;
}
package com.example.gymapp.dto.trainer;

import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.TrainingType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public  class TrainerResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private Boolean isActive;
    private Set<Trainee> trainees;
}
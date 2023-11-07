package com.example.gymapp.dto.trainee;

import com.example.gymapp.model.Trainer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;


@Data
public final class TraineeResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    //@JsonIgnoreProperties({"id", "password", "trainingList", "trainees", "isActive"})
    private Set<Trainer> trainers;
}


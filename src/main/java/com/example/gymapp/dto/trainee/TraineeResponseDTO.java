package com.example.gymapp.dto.trainee;

import com.example.gymapp.dto.user.UserDto;
import com.example.gymapp.model.Trainer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;


@Data
public final class TraineeResponseDTO {
    private UserDto userDto;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String address;
    private Set<Trainer> trainers;
}


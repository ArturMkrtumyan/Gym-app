package com.example.gymapp.dto.training;
import com.example.gymapp.model.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingResponseDTO {
    private String trainingName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    private TrainingType trainingType;
    private Integer trainingDuration;
    private String trainerName;
}

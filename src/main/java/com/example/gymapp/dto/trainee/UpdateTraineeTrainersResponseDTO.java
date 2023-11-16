package com.example.gymapp.dto.trainee;
import com.example.gymapp.model.TrainingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTraineeTrainersResponseDTO {
        private String username;
        private String firstName;
        private String lastName;
        private TrainingType specialization;
}

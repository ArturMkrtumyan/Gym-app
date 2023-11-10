package com.example.gymapp.util;

import com.example.gymapp.model.Training;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TrainingFilterUtil {
    public boolean isTrainingWithinPeriod(Training training, LocalDate periodFrom, LocalDate periodTo) {
        if (periodFrom != null && training.getTrainingDate().isBefore(periodFrom)) {
            return false;
        }

        return periodTo == null || !training.getTrainingDate().isAfter(periodTo);
    }

    public boolean isTrainingMatchingTrainer(Training training, String trainerName) {
        return trainerName == null || training.getTrainer().getUser().getFirstName().equalsIgnoreCase(trainerName);
    }

    public boolean isTrainingMatchingTrainingType(Training training, Long trainingTypeId) {
        return trainingTypeId == null || training.getTrainingType().getId().equals(trainingTypeId);
    }
}

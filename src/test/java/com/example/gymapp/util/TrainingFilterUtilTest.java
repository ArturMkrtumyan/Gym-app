package com.example.gymapp.util;


import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.model.TrainingType;
import com.example.gymapp.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainingFilterUtilTest {

    private final TrainingFilterUtil filterUtil = new TrainingFilterUtil();

    @Test
    void isTrainingWithinPeriod_TrainingBeforePeriod_ReturnsFalse() {

        Training training = mock(Training.class);
        when(training.getTrainingDate()).thenReturn(LocalDate.of(2022, 1, 1));

        boolean result = filterUtil.isTrainingWithinPeriod(training, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));

        assertFalse(result);
    }

    @Test
    void isTrainingWithinPeriod_TrainingWithinPeriod_ReturnsTrue() {
        Training training = mock(Training.class);
        when(training.getTrainingDate()).thenReturn(LocalDate.of(2023, 1, 1));

        boolean result = filterUtil.isTrainingWithinPeriod(training, LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));

        assertTrue(result);
    }

    @Test
    void isTrainingMatchingTrainer_MatchingTrainerName_ReturnsTrue() {
        Training training = mock(Training.class);
        Trainer trainer = mock(Trainer.class);
        User user = mock(User.class);
        when(user.getFirstName()).thenReturn("John");
        when(trainer.getUser()).thenReturn(user);
        when(training.getTrainer()).thenReturn(trainer);

        boolean result = filterUtil.isTrainingMatchingTrainer(training, "John");

        assertTrue(result);
    }

    @Test
    void isTrainingMatchingTrainingType_MatchingTrainingTypeId_ReturnsTrue() {
        Training training = mock(Training.class);
        TrainingType trainingType = mock(TrainingType.class);
        when(trainingType.getId()).thenReturn(1L);
        when(training.getTrainingType()).thenReturn(trainingType);

        boolean result = filterUtil.isTrainingMatchingTrainingType(training, 1L);

        assertTrue(result);
    }

}

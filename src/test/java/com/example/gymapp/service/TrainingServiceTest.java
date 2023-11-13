package com.example.gymapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import com.example.gymapp.dto.training.TrainingCreateRequestDTO;
import com.example.gymapp.exception.NoSuchTraineeExistException;
import com.example.gymapp.exception.NoSuchTrainerExistException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void testCreateTraining() {
        TrainingCreateRequestDTO trainingDTO = new TrainingCreateRequestDTO();
        trainingDTO.setTraineeUsername("traineeUsername");
        trainingDTO.setTrainerUsername("trainerUsername");
        trainingDTO.setTrainingName("Training Name");
        trainingDTO.setTrainingDate(LocalDate.of(2023, 11, 13));
        trainingDTO.setTrainingDuration(3);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        when(traineeService.findByUsername("traineeUsername")).thenReturn(Optional.of(trainee));
        when(trainerService.findByUsername("trainerUsername")).thenReturn(Optional.of(trainer));

        Training createdTraining = trainingService.createTraining(trainingDTO);

        assertNotNull(createdTraining);
        assertEquals(trainee, createdTraining.getTrainee());
        assertEquals(trainer, createdTraining.getTrainer());
        assertEquals("Training Name", createdTraining.getTrainingName());
        assertEquals(3, createdTraining.getTrainingDuration());

        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @Test
    void testCreateTrainingWithNonExistingTrainee() {
        TrainingCreateRequestDTO trainingDTO = new TrainingCreateRequestDTO();
        trainingDTO.setTraineeUsername("nonExistingTrainee");
        trainingDTO.setTrainerUsername("trainerUsername");

        when(traineeService.findByUsername("nonExistingTrainee")).thenReturn(Optional.empty());

        NoSuchTraineeExistException exception = assertThrows(NoSuchTraineeExistException.class, () -> {
            trainingService.createTraining(trainingDTO);
        });

        assertEquals("Trainee with username 'nonExistingTrainee' does not exist.", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void testCreateTrainingWithNonExistingTrainer() {
        TrainingCreateRequestDTO trainingDTO = new TrainingCreateRequestDTO();
        trainingDTO.setTraineeUsername("traineeUsername");
        trainingDTO.setTrainerUsername("nonExistingTrainer");

        when(traineeService.findByUsername("traineeUsername")).thenReturn(Optional.of(new Trainee()));
        when(trainerService.findByUsername("nonExistingTrainer")).thenReturn(Optional.empty());

        NoSuchTrainerExistException exception = assertThrows(NoSuchTrainerExistException.class, () -> {
            trainingService.createTraining(trainingDTO);
        });

        assertEquals("Trainer with username 'nonExistingTrainer' does not exist.", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
    }
}

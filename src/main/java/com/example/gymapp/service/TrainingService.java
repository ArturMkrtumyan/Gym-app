package com.example.gymapp.service;

import com.example.gymapp.dto.training.TrainingCreateRequestDTO;
import com.example.gymapp.exception.NoSuchTraineeExistException;
import com.example.gymapp.exception.NoSuchTrainerExistException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.repository.TrainingRepository;
import com.example.gymapp.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainingService {
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingRepository trainingRepository;

    public Training createTraining(TrainingCreateRequestDTO trainingDTO) {

        Optional<Trainee> traineeOpt = traineeService.findByUsername(trainingDTO.getTraineeUsername());
        if (traineeOpt.isEmpty()) {
            throw new NoSuchTraineeExistException("Trainee with username '" + trainingDTO.getTraineeUsername() + "' does not exist.");
        }

        Optional<Trainer> trainerOpt = trainerService.findByUsername(trainingDTO.getTrainerUsername());
        if (trainerOpt.isEmpty()) {
            throw new NoSuchTrainerExistException("Trainer with username '" + trainingDTO.getTrainerUsername() + "' does not exist.");

        }

        Training newTraining = Training.builder()
                .trainee(traineeOpt.get())
                .trainer(trainerOpt.get())
                .trainingName(trainingDTO.getTrainingName())
                .trainingType(trainerOpt.get().getSpecialization())
                .trainingDate(trainingDTO.getTrainingDate())
                .trainingDuration(trainingDTO.getTrainingDuration())
                .build();
        trainingRepository.save(newTraining);

        return newTraining;
    }
}

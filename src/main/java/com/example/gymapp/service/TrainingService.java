package com.example.gymapp.service;

import com.example.gymapp.dto.training.TrainingCreateRequestDTO;
import com.example.gymapp.exception.NoSuchTraineeExistException;
import com.example.gymapp.exception.NoSuchTrainerExistException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TrainingService {
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingRepository trainingRepository;

    public Training createTraining(TrainingCreateRequestDTO trainingDTO) {
        Trainee trainee = traineeService.findByUsername(trainingDTO.getTraineeUsername())
                .orElseThrow(() -> new NoSuchTraineeExistException("Trainee with username '" + trainingDTO.getTraineeUsername() + "' does not exist."));

        Trainer trainer = trainerService.findByUsername(trainingDTO.getTrainerUsername())
                .orElseThrow(() -> new NoSuchTrainerExistException("Trainer with username '" + trainingDTO.getTrainerUsername() + "' does not exist."));

        Training newTraining = new Training();
        newTraining.setTrainee(trainee);
        newTraining.setTrainer(trainer);
        newTraining.setTrainingName(trainingDTO.getTrainingName());
        newTraining.setTrainingType(trainer.getSpecialization());
        newTraining.setTrainingDate(trainingDTO.getTrainingDate());
        newTraining.setTrainingDuration(trainingDTO.getTrainingDuration());

        trainingRepository.save(newTraining);

        return newTraining;
    }

}

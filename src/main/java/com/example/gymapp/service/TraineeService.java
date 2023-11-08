package com.example.gymapp.service;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainee.TraineeCreateRequestDTO;
import com.example.gymapp.dto.trainee.TraineeRequestDTO;
import com.example.gymapp.dto.trainee.TraineeResponseDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.exception.TraineeNotFoundException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.repository.TraineeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TraineeService {
    private final ModelMapper modelMapper;
    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final TrainerService trainerService;

    public AuthDTO createTrainee(TraineeCreateRequestDTO traineeDTO) {
        String username = userService.generateUsername(traineeDTO.getFirstName(), traineeDTO.getLastName());
        String password = userService.generatePassword();

        Trainee newTrainee = modelMapper.map(traineeDTO, Trainee.class);
        newTrainee.setUsername(username);
        newTrainee.setPassword(password);

        newTrainee.setIsActive(true);

        Trainee createdTrainee = traineeRepository.save(newTrainee);

        return modelMapper.map(createdTrainee, AuthDTO.class);
    }

    public Optional<Trainee> findByUsername(String username) {
        return traineeRepository.findByUsername(username);
    }

    public TraineeResponseDTO updateTrainee(TraineeRequestDTO traineeDTO) {
        Trainee existingTrainee = traineeRepository.findTraineeProfileByUsername(traineeDTO.getUsername());

        modelMapper.map(traineeDTO, existingTrainee);

        authenticationService.authenticate(existingTrainee.getId(), existingTrainee.getUsername(), traineeDTO.getPassword());

        Trainee updatedTrainee = traineeRepository.save(existingTrainee);

        return modelMapper.map(updatedTrainee, TraineeResponseDTO.class);
    }


    public TraineeResponseDTO getTraineeProfile(String username, AuthDTO authDTO) {

        Trainee trainee = traineeRepository.findTraineeProfileByUsername(authDTO.getUsername());
        authenticationService.authenticate(trainee.getId(), username, authDTO.getPassword());

        Set<Trainer> trainers = trainee.getTrainers();
        trainee.setTrainers(trainers);

        return modelMapper.map(trainee, TraineeResponseDTO.class);
    }

    @Transactional
    public void changeActivationStatus(Long id, Boolean isActive, AuthDTO authDTO) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with ID: " + id));

        authenticationService.authenticate(id, authDTO.getUsername(), authDTO.getPassword());

        trainee.setIsActive(isActive);
        traineeRepository.save(trainee);
    }

    @Transactional
    public void deleteTrainee(Long id, AuthDTO authDTO) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with ID: " + id));

        authenticationService.authenticate(id, authDTO.getUsername(), authDTO.getPassword());

        traineeRepository.delete(trainee);
    }

    public List<TrainingResponseDTO> getTrainingsWithFiltering(Long id, TrainingGetListRequestDTO requestDTO) {
        authenticationService.authenticate(id, requestDTO.getUsername(), requestDTO.getPassword());

        List<Training> trainings = traineeRepository.findByTraineeUsernameAndTrainerFirstNameContainingAndTraineeFirstNameContaining
                (requestDTO.getUsername(), requestDTO.getTrainerName(), requestDTO.getTraineeName());
        return trainings.stream()
                .map(training -> modelMapper.map(training, TrainingResponseDTO.class))
                .toList();
    }


}
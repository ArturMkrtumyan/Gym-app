package com.example.gymapp.service;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainee.TraineeCreateRequestDTO;
import com.example.gymapp.dto.trainee.TraineeRequestDTO;
import com.example.gymapp.dto.trainee.TraineeResponseDTO;
import com.example.gymapp.dto.trainee.UpdateTraineeTrainersResponseDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.dto.user.UserDto;
import com.example.gymapp.exception.NoSuchTraineeExistException;
import com.example.gymapp.exception.TraineeNotFoundException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.TraineeRepository;
import com.example.gymapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class TraineeService {
    private final ModelMapper modelMapper;
    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final TrainerService trainerService;
    private final UserRepository userRepository;

    public AuthDTO createTrainee(TraineeCreateRequestDTO traineeDTO) {
        String username = userService.generateUsername(traineeDTO.getFirstName(), traineeDTO.getLastName());
        String password = userService.generatePassword();

        User newUser = User.builder()
                .username(username)
                .password(password)
                .firstName(traineeDTO.getFirstName())
                .lastName(traineeDTO.getLastName())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);

        Trainee newTrainee = modelMapper.map(traineeDTO, Trainee.class);
        newTrainee.setUser(savedUser);

        traineeRepository.save(newTrainee);

        return modelMapper.map(savedUser, AuthDTO.class);
    }


    public Optional<Trainee> findByUsername(String username) {
        return traineeRepository.findByUserUsername(username);
    }

    public TraineeResponseDTO updateTrainee(TraineeRequestDTO traineeDTO) {
        authenticationService.authenticate(traineeDTO.getUsername(), traineeDTO.getPassword());

        Trainee existingTrainee = traineeRepository.findByUserUsername(traineeDTO.getUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found for username: " + traineeDTO.getUsername()));
        modelMapper.map(traineeDTO, existingTrainee);
        Trainee updatedTrainee = traineeRepository.save(existingTrainee);

        return modelMapper.map(updatedTrainee, TraineeResponseDTO.class);

    }


    public TraineeResponseDTO getTraineeProfile(AuthDTO authDTO) {
        authenticationService.authenticate(authDTO.getUsername(), authDTO.getPassword());
        Trainee trainee = traineeRepository.findByUserUsername(authDTO.getUsername()).orElseThrow();

        UserDto userDto = modelMapper.map(trainee.getUser(), UserDto.class);
        return TraineeResponseDTO.builder()
                .userDto(userDto)
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .trainers(trainee.getTrainers())
                .build();
    }

    @Transactional
    public void changeActivationStatus(Long id, Boolean isActive, AuthDTO authDTO) {
        authenticationService.authenticate(authDTO.getUsername(), authDTO.getPassword());

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with ID: " + id));

        trainee.getUser().setIsActive(isActive);
        traineeRepository.save(trainee);
    }

    @Transactional
    public void deleteTrainee(Long id, AuthDTO authDTO) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with ID: " + id));

        authenticationService.authenticate(authDTO.getUsername(), authDTO.getPassword());

        traineeRepository.delete(trainee);
    }


    public List<TrainingResponseDTO> getTrainingsWithFiltering(Long id, TrainingGetListRequestDTO requestDTO) {
        authenticationService.authenticate(requestDTO.getUsername(), requestDTO.getPassword());

        if (!traineeRepository.existsById(id)) {
            throw new TraineeNotFoundException("Trainee not found with id: " + id);
        }
        List<Training> filteredTrainings = traineeRepository.findTrainingsByTraineeWithFiltering(
                id,
                requestDTO.getPeriodFrom(),
                requestDTO.getPeriodTo(),
                requestDTO.getTrainerName(),
                requestDTO.getTrainingType()
        );

        return filteredTrainings.stream()
                .map(training -> modelMapper.map(training, TrainingResponseDTO.class))
                .toList();
    }


    @Transactional
    public List<UpdateTraineeTrainersResponseDTO> updateTrainerList(Long id, TraineeRequestDTO traineeRequestDTO) {
        List<Trainer> updatedList = trainerService.getVerifiedTrainersByUsernameList(traineeRequestDTO.getTrainers());

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new NoSuchTraineeExistException("Trainee is not exist"));

        trainee.setTrainers(new HashSet<>(updatedList));
        traineeRepository.save(trainee);

        log.info("Successfully updated trainee's list of trainers for id '{}' with '{}' trainers", id, updatedList.size());

        return updatedList.stream()
                .map(trainer -> modelMapper.map(trainer, UpdateTraineeTrainersResponseDTO.class))
                .toList();
    }


    public List<TrainerResponseDTO> getPotentialTrainersForTrainee(long id) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new NoSuchTraineeExistException("Trainee is not exist"));

        List<Trainer> allTrainers = trainerService.findAll();
        Set<Trainer> assignedTrainers = trainee.getTrainers();

        return allTrainers.stream()
                .filter(trainer -> !assignedTrainers.contains(trainer))
                .filter(trainer -> trainer.getUser().getIsActive())
                .map(trainer -> modelMapper.map(trainer, TrainerResponseDTO.class))
                .toList();
    }

}
package com.example.gymapp.service;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainee.TraineeCreateRequestDTO;
import com.example.gymapp.dto.trainee.TraineeRequestDTO;
import com.example.gymapp.dto.trainee.TraineeResponseDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.dto.user.UserDto;
import com.example.gymapp.exception.NoSuchTraineeExistException;
import com.example.gymapp.exception.TraineeNotFoundException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.TraineeRepository;
import com.example.gymapp.repository.UserRepository;
import com.example.gymapp.util.TrainingFilterUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final UserRepository userRepository;
    private final TrainingFilterUtil trainingFilterUtil;

    public AuthDTO createTrainee(TraineeCreateRequestDTO traineeDTO) {
        String username = userService.generateUsername(traineeDTO.getFirstName(), traineeDTO.getLastName());
        String password = userService.generatePassword();

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFirstName(traineeDTO.getFirstName());
        newUser.setLastName(traineeDTO.getLastName());
        newUser.setIsActive(true);

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
        Trainee existingTrainee = traineeRepository.findByUserUsername(traineeDTO.getUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found for username: " + traineeDTO.getUsername()));

        User user = existingTrainee.getUser();
        user.setFirstName(traineeDTO.getFirstName());
        user.setLastName(traineeDTO.getLastName());
        user.setIsActive(traineeDTO.getIsActive());

        existingTrainee.setDateOfBirth(traineeDTO.getDateOfBirth());
        existingTrainee.setAddress(traineeDTO.getAddress());

        traineeRepository.save(existingTrainee);

        UserDto userDto = modelMapper.map(existingTrainee.getUser(), UserDto.class);
        TraineeResponseDTO traineeResponseDTO = new TraineeResponseDTO();
        traineeResponseDTO.setUserDto(userDto);
        traineeResponseDTO.setAddress(traineeDTO.getAddress());
        traineeResponseDTO.setDateOfBirth(traineeDTO.getDateOfBirth());
        traineeResponseDTO.setTrainers(existingTrainee.getTrainers());

        return traineeResponseDTO;
    }


    public TraineeResponseDTO getTraineeProfile(AuthDTO authDTO) {
        Trainee trainee = traineeRepository.findByUserUsername(authDTO.getUsername()).orElseThrow();
        authenticationService.authenticate(trainee.getUser().getId(), authDTO.getUsername(), authDTO.getPassword());

        TraineeResponseDTO responseDTO = new TraineeResponseDTO();
        UserDto userDto = modelMapper.map(trainee.getUser(), UserDto.class);
        responseDTO.setUserDto(userDto);
        responseDTO.setDateOfBirth(trainee.getDateOfBirth());
        responseDTO.setAddress(trainee.getAddress());
        responseDTO.setTrainers(trainee.getTrainers());
        return responseDTO;
    }


    @Transactional
    public void changeActivationStatus(Long id, Boolean isActive, AuthDTO authDTO) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with ID: " + id));

        authenticationService.authenticate(id, authDTO.getUsername(), authDTO.getPassword());

        trainee.getUser().setIsActive(isActive);
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

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with id: " + id));

        return trainee.getTrainingList().stream()
                .filter(training -> trainingFilterUtil.isTrainingWithinPeriod(training, requestDTO.getPeriodFrom(), requestDTO.getPeriodTo()))
                .filter(training -> trainingFilterUtil.isTrainingMatchingTrainer(training, requestDTO.getTrainerName()))
                .filter(training -> trainingFilterUtil.isTrainingMatchingTrainingType(training, requestDTO.getTrainingType()))
                .map(training -> modelMapper.map(training, TrainingResponseDTO.class))
                .toList();
    }

    public List<TrainerResponseDTO> updateTrainerList(Long id, TraineeRequestDTO traineeDTO) {
        return new ArrayList<>();
    }


    public List<TrainerResponseDTO> getPotentialTrainersForTrainee( long id) {
        Optional<Trainee> traineeOpt = traineeRepository.findById(id);

        if (traineeOpt.isEmpty()) {
            throw new NoSuchTraineeExistException("Trainee is not exist");
        }

        List<Trainer> allTrainers = trainerService.findAll();
        Set<Trainer> assignedTrainers = traineeOpt.get().getTrainers();

        return  allTrainers.stream()
                .filter(trainer -> !assignedTrainers.contains(trainer))
                .filter(trainer -> trainer.getUser().getIsActive())
                .map(trainer -> modelMapper.map(trainer, TrainerResponseDTO.class))
                .toList();
    }

}
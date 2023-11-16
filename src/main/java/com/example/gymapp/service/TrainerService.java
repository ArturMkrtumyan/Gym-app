package com.example.gymapp.service;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainer.TrainerRequestDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.trainer.UpdateTrainerDto;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.exception.NoSuchTrainerExistException;
import com.example.gymapp.exception.TrainerNotFoundException;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.model.TrainingType;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.TrainerRepository;
import com.example.gymapp.repository.TrainingTypeRepository;
import com.example.gymapp.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TrainingTypeRepository trainingTypeRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;


    public AuthDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        String username = userService.generateUsername(trainerRequestDTO.getFirstName(), trainerRequestDTO.getLastName());
        String password = userService.generatePassword();

        TrainingType specialization = trainingTypeRepository.findById(trainerRequestDTO.getSpecialization())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found with ID: " + trainerRequestDTO.getSpecialization()));
        User newUser = User.builder()
                .username(username)
                .password(password)
                .firstName(trainerRequestDTO.getFirstName())
                .lastName(trainerRequestDTO.getLastName())
                .isActive(true)
                .build();
        userRepository.save(newUser);

        Trainer newTrainer = modelMapper.map(trainerRequestDTO, Trainer.class);
        newTrainer.setSpecialization(specialization);
        newTrainer.setUser(newUser);

        trainerRepository.save(newTrainer);

        return modelMapper.map(newUser, AuthDTO.class);
    }

    public Optional<Trainer> findByUsername(String username) {
        return trainerRepository.findByUserUsername(username);
    }


    public Set<Trainer> findTrainersByTraineeId(Long id) {
        return trainerRepository.findTrainersByTraineeId(id);
    }

    public TrainerResponseDTO getTrainerProfile(AuthDTO authDTO) {
        Trainer trainer = trainerRepository.findByUserUsername(authDTO.getUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + authDTO.getUsername()));
        authenticationService.authenticate( authDTO.getUsername(), authDTO.getPassword());
        return modelMapper.map(trainer, TrainerResponseDTO.class);
    }


    public TrainerResponseDTO updateTrainer(UpdateTrainerDto updateTrainerDto) {
        authenticationService.authenticate(updateTrainerDto.getUsername(), updateTrainerDto.getPassword());
        Trainer existingTrainer = trainerRepository.findTrainerProfileByUsername(updateTrainerDto.getUsername());

        modelMapper.map(updateTrainerDto, existingTrainer);
        Trainer updatedTrainer = trainerRepository.save(existingTrainer);

        return modelMapper.map(updatedTrainer, TrainerResponseDTO.class);
    }

    @Transactional
    public void changeActivationStatus(Long id, Boolean isActive, AuthDTO authDTO) {
        authenticationService.authenticate( authDTO.getUsername(), authDTO.getPassword());

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with ID: " + id));

        trainer.getUser().setIsActive(isActive);
        trainerRepository.save(trainer);
    }

    public List<TrainingResponseDTO> getTrainingsWithFiltering(Long id, TrainingGetListRequestDTO requestDTO) {
        authenticationService.authenticate(requestDTO.getUsername(), requestDTO.getPassword());

        if (!trainerRepository.existsById(id)) {
            throw new TrainerNotFoundException("Trainer not found with id: " + id);
        }

        List<Training> filteredTrainings = trainerRepository.findTrainingsByTrainerWithFiltering(
                id,
                requestDTO.getPeriodFrom(),
                requestDTO.getPeriodTo(),
                requestDTO.getTraineeName(),
                requestDTO.getTrainingType()
        );

        return filteredTrainings.stream()
                .map(training -> modelMapper.map(training, TrainingResponseDTO.class))
                .toList();
    }

    public List<Trainer> findAll() {
       return trainerRepository.findAll();
    }
    public List<Trainer> getVerifiedTrainersByUsernameList(List<String> trainerUsernames) {
        List<Trainer> verifiedList = findAll().stream()
                .filter(trainer -> trainerUsernames.contains(trainer.getUser().getUsername()))
                .toList();

        if (verifiedList.isEmpty()) {
            throw new NoSuchTrainerExistException("Such Trainer is not exist");
        }

        return verifiedList;
    }
}


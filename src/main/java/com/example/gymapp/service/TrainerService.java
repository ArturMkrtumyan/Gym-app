package com.example.gymapp.service;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainer.TrainerRequestDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.trainer.UpdateTrainerDto;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.exception.TrainerNotFoundException;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import com.example.gymapp.model.TrainingType;
import com.example.gymapp.repository.TrainerRepository;
import com.example.gymapp.repository.TrainingTypeRepository;
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


    public AuthDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        String username = userService.generateUsername(trainerRequestDTO.getFirstName(), trainerRequestDTO.getLastName());
        String password = userService.generatePassword();

        TrainingType specialization = trainingTypeRepository.findById(trainerRequestDTO.getSpecialization())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found with ID: " + trainerRequestDTO.getSpecialization()));

        Trainer newTrainer = modelMapper.map(trainerRequestDTO, Trainer.class);
        newTrainer.setSpecialization(specialization);
        newTrainer.setUsername(username);
        newTrainer.setPassword(password);
        newTrainer.setIsActive(true);

        Trainer createdTrainer = trainerRepository.save(newTrainer);

        return modelMapper.map(createdTrainer, AuthDTO.class);
    }

    public Optional<Trainer> findByUsername(String username) {
        return trainerRepository.findByUsername(username);
    }


    public Set<Trainer> findTrainersByTraineeId(Long id) {
        return trainerRepository.findTrainersByTraineeId(id);
    }

    public TrainerResponseDTO getTrainerProfile(String username,AuthDTO authDTO) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with username: " + username));
        authenticationService.authenticate(trainer.getId(),authDTO.getUsername(),authDTO.getPassword());
        return modelMapper.map(trainer, TrainerResponseDTO.class);
    }

    public TrainerResponseDTO updateTrainer(UpdateTrainerDto updateTrainerDto) {
        Trainer existingTrainer = trainerRepository.findTrainerProfileByUsername(updateTrainerDto.getUsername());

        modelMapper.map(updateTrainerDto, existingTrainer);

        authenticationService.authenticate(existingTrainer.getId(), existingTrainer.getUsername(), updateTrainerDto.getPassword());

        Trainer updatedTrainer = trainerRepository.save(existingTrainer);

        return modelMapper.map(updatedTrainer, TrainerResponseDTO.class);
    }

    @Transactional
    public void changeActivationStatus(Long id, Boolean isActive, AuthDTO authDTO) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with ID: " + id));

        authenticationService.authenticate(id, authDTO.getUsername(), authDTO.getPassword());

        trainer.setIsActive(isActive);
        trainerRepository.save(trainer);
    }

    public List<TrainingResponseDTO> getTrainingsWithFiltering(Long id, TrainingGetListRequestDTO requestDTO) {
        authenticationService.authenticate(id, requestDTO.getUsername(), requestDTO.getPassword());

        List<Training> trainings = trainerRepository.findByTraineeUsernameAndTrainerFirstNameContainingAndTraineeFirstNameContaining
                (requestDTO.getUsername(), requestDTO.getTrainerName(), requestDTO.getTraineeName());
        return trainings.stream()
                .map(training -> modelMapper.map(training, TrainingResponseDTO.class))
                .toList();
    }
}


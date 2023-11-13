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
import com.example.gymapp.model.TrainingType;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.TrainerRepository;
import com.example.gymapp.repository.TrainingTypeRepository;
import com.example.gymapp.repository.UserRepository;
import com.example.gymapp.util.TrainingFilterUtil;
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
    private final TrainingFilterUtil trainingFilterUtil;


    public AuthDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        String username = userService.generateUsername(trainerRequestDTO.getFirstName(), trainerRequestDTO.getLastName());
        String password = userService.generatePassword();

        TrainingType specialization = trainingTypeRepository.findById(trainerRequestDTO.getSpecialization())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found with ID: " + trainerRequestDTO.getSpecialization()));
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFirstName(trainerRequestDTO.getFirstName());
        newUser.setLastName(trainerRequestDTO.getLastName());
        newUser.setIsActive(true);
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
        authenticationService.authenticate(trainer.getUser().getId(), authDTO.getUsername(), authDTO.getPassword());
        return modelMapper.map(trainer, TrainerResponseDTO.class);
    }


    public TrainerResponseDTO updateTrainer(UpdateTrainerDto updateTrainerDto) {
        Trainer existingTrainer = trainerRepository.findTrainerProfileByUsername(updateTrainerDto.getUsername());

        modelMapper.map(updateTrainerDto, existingTrainer);

        authenticationService.authenticate(existingTrainer.getUser().getId(), existingTrainer.getUser().getUsername(), updateTrainerDto.getPassword());

        Trainer updatedTrainer = trainerRepository.save(existingTrainer);

        return modelMapper.map(updatedTrainer, TrainerResponseDTO.class);
    }

    @Transactional
    public void changeActivationStatus(Long id, Boolean isActive, AuthDTO authDTO) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with ID: " + id));

        authenticationService.authenticate(id, authDTO.getUsername(), authDTO.getPassword());

        trainer.getUser().setIsActive(isActive);
        trainerRepository.save(trainer);
    }

    public List<TrainingResponseDTO> getTrainingsWithFiltering(Long id, TrainingGetListRequestDTO requestDTO) {
        authenticationService.authenticate(id, requestDTO.getUsername(), requestDTO.getPassword());

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found with id: " + id));

        return trainer.getTrainingList().stream()
                .filter(training -> trainingFilterUtil.isTrainingWithinPeriod(training, requestDTO.getPeriodFrom(), requestDTO.getPeriodTo()))
                .filter(training -> trainingFilterUtil.isTrainingMatchingTrainer(training, requestDTO.getTrainerName()))
                .filter(training -> trainingFilterUtil.isTrainingMatchingTrainingType(training, requestDTO.getTrainingType()))
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


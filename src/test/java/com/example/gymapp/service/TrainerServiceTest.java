package com.example.gymapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainer.TrainerRequestDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.trainer.UpdateTrainerDto;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.TrainingType;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.TrainerRepository;
import com.example.gymapp.repository.TrainingTypeRepository;
import com.example.gymapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private TrainerService trainerService;

    @Test
    void testCreateTrainer() {
        TrainerRequestDTO trainerDTO = new TrainerRequestDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setSpecialization(1L);

        String generatedUsername = "johndoe";
        String generatedPassword = "password123";

        when(userService.generateUsername("John", "Doe")).thenReturn(generatedUsername);
        when(userService.generatePassword()).thenReturn(generatedPassword);

        User savedUser = new User();
        savedUser.setUsername(generatedUsername);
        savedUser.setPassword(generatedPassword);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setIsActive(true);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        TrainingType specialization = new TrainingType();
        specialization.setId(1L);
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(specialization));

        Trainer mappedTrainer = new Trainer();
        when(modelMapper.map(trainerDTO, Trainer.class)).thenReturn(mappedTrainer);

        Trainer newTrainer = new Trainer();
        newTrainer.setSpecialization(specialization);
        newTrainer.setUser(savedUser);

        when(modelMapper.map(trainerDTO, Trainer.class)).thenReturn(newTrainer);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(generatedUsername);
        authDTO.setPassword(generatedPassword);

        when(modelMapper.map(savedUser, AuthDTO.class)).thenReturn(authDTO);

        AuthDTO result = trainerService.createTrainer(trainerDTO);

        assertNotNull(result);
        assertEquals(generatedUsername, result.getUsername());
        assertEquals(generatedPassword, result.getPassword());

        verify(userRepository, times(1)).save(any(User.class));
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

}

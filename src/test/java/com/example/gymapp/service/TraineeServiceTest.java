package com.example.gymapp.service;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainee.TraineeCreateRequestDTO;
import com.example.gymapp.dto.trainee.TraineeRequestDTO;
import com.example.gymapp.dto.trainee.TraineeResponseDTO;
import com.example.gymapp.dto.user.UserDto;
import com.example.gymapp.exception.TraineeNotFoundException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.TraineeRepository;
import com.example.gymapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TraineeService traineeService;

    @Test
    void testCreateTrainee() {
        // Arrange
        TraineeCreateRequestDTO traineeDTO = new TraineeCreateRequestDTO();
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");

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

        Trainee mappedTrainee = new Trainee();
        when(modelMapper.map(traineeDTO, Trainee.class)).thenReturn(mappedTrainee);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(generatedUsername);
        authDTO.setPassword(generatedPassword);

        when(modelMapper.map(savedUser, AuthDTO.class)).thenReturn(authDTO);

        AuthDTO result = traineeService.createTrainee(traineeDTO);

        assertNotNull(result);
        assertEquals(generatedUsername, result.getUsername());
        assertEquals(generatedPassword, result.getPassword());

        verify(userRepository, times(1)).save(any(User.class));
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() {
        TraineeRequestDTO traineeDTO = new TraineeRequestDTO();
        traineeDTO.setUsername("testUsername");
        traineeDTO.setPassword("testPassword");
        traineeDTO.setFirstName("UpdatedFirstName");
        traineeDTO.setLastName("UpdatedLastName");
        traineeDTO.setIsActive(true);
        traineeDTO.setDateOfBirth(LocalDate.parse("1990-01-01"));
        traineeDTO.setAddress("UpdatedAddress");

        Trainee existingTrainee = new Trainee();
        existingTrainee.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setIsActive(true);

        existingTrainee.setUser(user);
        existingTrainee.setDateOfBirth(LocalDate.parse("1990-01-01"));
        existingTrainee.setAddress("123 Main St");

        Set<Trainer> trainers = new HashSet<>();
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainers.add(trainer1);

        existingTrainee.setTrainers(trainers);

        when(traineeRepository.findByUserUsername("testUsername")).thenReturn(Optional.of(existingTrainee));
        doNothing().when(authenticationService).authenticate(1L, "testUsername", "testPassword");

        TraineeResponseDTO result = traineeService.updateTrainee(traineeDTO);

        assertNotNull(result);
        assertEquals(traineeDTO.getFirstName(), existingTrainee.getUser().getFirstName());
        assertEquals(traineeDTO.getLastName(), existingTrainee.getUser().getLastName());
        assertEquals(traineeDTO.getIsActive(), existingTrainee.getUser().getIsActive());
        assertEquals(traineeDTO.getDateOfBirth(), existingTrainee.getDateOfBirth());
        assertEquals(traineeDTO.getAddress(), existingTrainee.getAddress());
        assertEquals(traineeDTO.getAddress(), result.getAddress());
        assertEquals(traineeDTO.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(existingTrainee.getTrainers(), result.getTrainers());

        verify(traineeRepository, times(1)).findByUserUsername("testUsername");
        verify(authenticationService, times(1)).authenticate(1L, "testUsername", "testPassword");
        verify(traineeRepository, times(1)).save(existingTrainee);
        verify(modelMapper, times(1)).map(existingTrainee.getUser(), UserDto.class);
    }
    @Test
    void testDeleteTrainee() {
        Long traineeId = 1L;

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("testUsername");
        authDTO.setPassword("testPassword");

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        when(traineeRepository.findById(eq(traineeId))).thenReturn(Optional.of(trainee));
        doNothing().when(authenticationService).authenticate(eq(traineeId), eq(authDTO.getUsername()), eq(authDTO.getPassword()));

        traineeService.deleteTrainee(traineeId, authDTO);

        verify(traineeRepository, times(1)).findById(eq(traineeId));
        verify(authenticationService, times(1)).authenticate(eq(traineeId), eq(authDTO.getUsername()), eq(authDTO.getPassword()));
        verify(traineeRepository, times(1)).delete(eq(trainee));
    }
    @Test
    void testDeleteTrainee_ThrowsExceptionWhenTraineeNotFound() {
        Long traineeId = 1L;

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("testUsername");
        authDTO.setPassword("testPassword");

        when(traineeRepository.findById(eq(traineeId))).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> traineeService.deleteTrainee(traineeId, authDTO));

        verify(authenticationService, never()).authenticate(any(), any(), any());
        verify(traineeRepository, never()).delete(any());
    }

}

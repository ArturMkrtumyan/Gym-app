package com.example.gymapp.service;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainee.TraineeCreateRequestDTO;
import com.example.gymapp.exception.TraineeNotFoundException;
import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.TraineeRepository;
import com.example.gymapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

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
    void testDeleteTrainee() {
        Long traineeId = 1L;

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("testUsername");
        authDTO.setPassword("testPassword");

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        when(traineeRepository.findById(eq(traineeId))).thenReturn(Optional.of(trainee));
        doNothing().when(authenticationService).authenticate( eq(authDTO.getUsername()), eq(authDTO.getPassword()));

        traineeService.deleteTrainee(traineeId, authDTO);

        verify(traineeRepository, times(1)).findById(eq(traineeId));
        verify(authenticationService, times(1)).authenticate( eq(authDTO.getUsername()), eq(authDTO.getPassword()));
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

        verify(authenticationService, never()).authenticate(any(), any());
        verify(traineeRepository, never()).delete(any());
    }

}

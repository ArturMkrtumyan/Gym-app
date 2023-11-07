package com.example.gymapp.controller;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainee.TraineeCreateRequestDTO;
import com.example.gymapp.dto.trainee.TraineeRequestDTO;
import com.example.gymapp.dto.trainee.TraineeResponseDTO;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trainees")
public class TraineeController {
    private final TraineeService traineeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);

    @PostMapping
    @Operation(summary = "Create new trainee",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Trainee created successfully"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")})
    public ResponseEntity<?> create(@Valid @RequestBody TraineeCreateRequestDTO traineeDTO) {
        var responseDTO = traineeService.createTrainee(traineeDTO);

        LOGGER.info("Trainee created successfully. Status: {}", HttpStatus.CREATED);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping()
    @Operation(summary = "Update Trainee profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")
            })
    public ResponseEntity<?> updateTrainee(@Valid @RequestBody TraineeRequestDTO traineeDTO) {
        var responseDTO = traineeService.updateTrainee(traineeDTO);

        if (responseDTO != null) {
            LOGGER.info("Trainee profile updated successfully. Status: {}", HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            LOGGER.warn("Trainee with ID {} not found. Status: {}", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get Trainee Profile by Username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")
            })
    public ResponseEntity<TraineeResponseDTO> getTraineeProfile(@PathVariable String username, @Valid @RequestBody AuthDTO authDTO) {
        var responseDTO = traineeService.getTraineeProfile(username, authDTO);

        if (responseDTO != null) {
            LOGGER.info("Trainee profile retrieved successfully. Status: {}", HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            LOGGER.warn("Trainee with username {} not found. Status: {}", username, HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate and deactivate trainee",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Changed activation status successfully"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials"),
                    @ApiResponse(responseCode = "403", description = "Access denied (wrong ID?)"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")
            })
    public ResponseEntity<?> handleChangeActivationStatus(@PathVariable("id") Long id, @Valid @RequestBody AuthDTO authDTO,
                                                          @RequestParam(name = "isActive", required = true) Boolean isActive) {
        traineeService.changeActivationStatus(id, isActive, authDTO);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete trainee",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Trainee deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials"),
                    @ApiResponse(responseCode = "403", description = "Access denied (wrong ID?)"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found"),
                    @ApiResponse(responseCode = "422", description = "Username or password is null")
            })
    public ResponseEntity<?> deleteTrainee(@PathVariable("id") long id, @RequestBody @Valid AuthDTO authDTO) {
        traineeService.deleteTrainee(id, authDTO);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/trainings")
    @Operation(summary = "Get all trainings for trainee with filtering",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of trainings retrieved"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials"),
                    @ApiResponse(responseCode = "403", description = "Access denied (wrong ID?)"),
                    @ApiResponse(responseCode = "404", description = "Trainer not found"),
                    @ApiResponse(responseCode = "422", description = "Username or password is null")})
    public ResponseEntity<?> getTrainingsWithFiltering(@PathVariable("id") Long id,
                                                       @RequestBody @Valid TrainingGetListRequestDTO requestDTO) {
        List<TrainingResponseDTO> responseDTO= traineeService.getTrainingsWithFiltering(id, requestDTO);

        return ResponseEntity.ok().body(responseDTO);
    }

}



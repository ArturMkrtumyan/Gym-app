package com.example.gymapp.controller;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainee.TraineeCreateRequestDTO;
import com.example.gymapp.dto.trainee.TraineeRequestDTO;
import com.example.gymapp.dto.trainee.TraineeResponseDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trainees")
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    @Operation(summary = "Create new trainee",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Trainee created successfully"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")})
    public ResponseEntity<AuthDTO> create(@Valid @RequestBody TraineeCreateRequestDTO traineeDTO) {
        var responseDTO = traineeService.createTrainee(traineeDTO);

        log.info("Trainee created successfully. Status: {}", HttpStatus.CREATED);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping()
    @Operation(summary = "Update Trainee profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")
            })
    public ResponseEntity<TraineeResponseDTO> updateTrainee(@Valid @RequestBody TraineeRequestDTO traineeDTO) {
        var responseDTO = traineeService.updateTrainee(traineeDTO);

        log.info("Trainee profile updated successfully. Status: {}", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/trainee")
    @Operation(summary = "Get Trainee Profile by Username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found")})
    public ResponseEntity<TraineeResponseDTO> getTraineeProfile(@Valid @RequestBody AuthDTO authDTO) {
        var responseDTO = traineeService.getTraineeProfile(authDTO);

        if (responseDTO != null) {
            log.info("Trainee profile retrieved successfully. Status: {}", HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            log.warn("Trainee with username {} not found. Status: {}", authDTO.getUsername(), HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @PatchMapping("/trainee/{id}/Activate/De-Activate")
    @Operation(summary = "Activate and deactivate trainee",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Changed activation status successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found")})
    public ResponseEntity<Void> changeActivationStatus(@PathVariable("id") Long id, @Valid @RequestBody AuthDTO authDTO,
                                                       @RequestParam(name = "isActive") Boolean isActive) {
        traineeService.changeActivationStatus(id, isActive, authDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete trainee",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Trainee deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found"),
            })
    public ResponseEntity<Void> deleteTrainee(@PathVariable("id") Long id, @RequestBody @Valid AuthDTO authDTO) {
        traineeService.deleteTrainee(id, authDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/trainings")
    @Operation(summary = "Get all trainings for trainee with filtering",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of trainings retrieved"),
                    @ApiResponse(responseCode = "404", description = "Trainer not found")})
    public ResponseEntity<List<TrainingResponseDTO>> getTrainingsWithFiltering(@PathVariable("id") Long id,
                                                                               @RequestBody @Valid TrainingGetListRequestDTO requestDTO) {
        var responseDTO = traineeService.getTrainingsWithFiltering(id, requestDTO);

        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}/trainers")
    @Operation(summary = "Update trainee's list of assigned trainers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of trainers successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found")})
    public ResponseEntity<List<TrainerResponseDTO>> updateTraineeTrainerList(@PathVariable("id") Long id,
                                                                             @RequestBody TraineeRequestDTO traineeDTO) {
        var updatedTrainersList = traineeService.updateTrainerList(id, traineeDTO);

        return ResponseEntity.ok(updatedTrainersList);
    }

    @GetMapping("/{id}/unassigned-trainers")
    @Operation(summary = "Get unassigned trainers for trainee",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of potential trainers retrieved"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found")})
    public ResponseEntity<List<TrainerResponseDTO>> getUnassignedTrainersForTrainee(@PathVariable("id") Long id) {

        List<TrainerResponseDTO> responseDTO = traineeService.getPotentialTrainersForTrainee(id);

        return ResponseEntity.ok().body(responseDTO);
    }
}




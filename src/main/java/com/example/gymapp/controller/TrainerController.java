package com.example.gymapp.controller;

import com.example.gymapp.dto.auth.AuthDTO;
import com.example.gymapp.dto.trainer.TrainerRequestDTO;
import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.dto.trainer.UpdateTrainerDto;
import com.example.gymapp.dto.training.TrainingGetListRequestDTO;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.service.TrainerService;
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
@RequestMapping(value = "/api/v1/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "Create new trainer",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Trainer created successfully"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")})
    public ResponseEntity<AuthDTO> create(@Valid @RequestBody TrainerRequestDTO trainerRequestDTO) {
        var responseDTO = trainerService.createTrainer(trainerRequestDTO);

        log.info("Trainer created successfully. Status: {}", HttpStatus.CREATED);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/trainer")
    @Operation(summary = "Get Trainer Profile by Username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainer profile retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainer not found")})
    public ResponseEntity<TrainerResponseDTO> getTrainerProfile(@Valid @RequestBody AuthDTO authDTO) {
        TrainerResponseDTO responseDTO = trainerService.getTrainerProfile(authDTO);

        if (responseDTO != null) {
            log.info("Trainer profile retrieved successfully. Status: {}", HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            log.warn("Trainer with username {} not found. Status: {}", authDTO.getUsername(), HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @PutMapping()
    @Operation(summary = "Update Trainer profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainer not found")})
    public ResponseEntity<TrainerResponseDTO> updateTrainer(@Valid @RequestBody UpdateTrainerDto updateTrainerDto) {
        var responseDTO = trainerService.updateTrainer(updateTrainerDto);

        if (responseDTO != null) {
            log.info("Trainer profile updated successfully. Status: {}", HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            log.warn("Trainer with ID {} not found.", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @PatchMapping("/trainer/{id}/Activate/De-Activate")
    @Operation(summary = "Activate and deactivate trainer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Changed activation status successfully"),
                    @ApiResponse(responseCode = "404", description = "Trainer not found"),
            })
    public ResponseEntity<Void> trainerActivationStatus(@PathVariable("id") Long id, @Valid @RequestBody AuthDTO authDTO,
                                                        @RequestParam(name = "isActive") Boolean isActive) {
        trainerService.changeActivationStatus(id, isActive, authDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/trainings")
    @Operation(summary = "Get all trainings for trainer with filtering",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of trainings retrieved"),
                    @ApiResponse(responseCode = "404", description = "Trainer not found")})
    public ResponseEntity<List<TrainingResponseDTO>> getTrainingsWithFiltering(@PathVariable("id") Long id,
                                                                               @RequestBody @Valid TrainingGetListRequestDTO requestDTO) {
        List<TrainingResponseDTO> responseDTO = trainerService.getTrainingsWithFiltering(id, requestDTO);

        return ResponseEntity.ok().body(responseDTO);
    }

}

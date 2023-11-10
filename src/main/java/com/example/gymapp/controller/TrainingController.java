package com.example.gymapp.controller;

import com.example.gymapp.dto.training.TrainingCreateRequestDTO;
import com.example.gymapp.model.Training;
import com.example.gymapp.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/trainings", produces = {"application/JSON"})
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping(consumes = {"application/JSON"})
    @Operation(summary = "Create new training",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Training created successfully"),
                    @ApiResponse(responseCode = "422", description = "Bad input, check body for error messages")})
    public ResponseEntity<Training> addTraining(
            @RequestBody @Valid TrainingCreateRequestDTO trainingDTO
    ) {
        var createdTraining = trainingService.createTraining(trainingDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTraining);
    }
}

package com.example.gymapp.mapper;

import com.example.gymapp.dto.trainee.UpdateTraineeTrainersResponseDTO;
import com.example.gymapp.model.Trainer;
import org.modelmapper.PropertyMap;

public class UpdateTraineeTrainerMapping extends PropertyMap<Trainer, UpdateTraineeTrainersResponseDTO> {
    @Override
    protected void configure() {
        map().setUsername(source.getUser().getUsername());
        map().setFirstName(source.getUser().getFirstName());
        map().setLastName(source.getUser().getLastName());
    }
}

package com.example.gymapp.mapper;

import com.example.gymapp.dto.trainee.TraineeResponseDTO;
import com.example.gymapp.model.Trainee;
import org.modelmapper.PropertyMap;

public class TraineeTraineeResponseDTOMapping extends PropertyMap<Trainee, TraineeResponseDTO>{
    @Override
    protected void configure() {
        map().setAddress(source.getAddress());
        map().setDateOfBirth(source.getDateOfBirth());
        map().getUserDto().setFirstName(source.getUser().getFirstName());
        map().getUserDto().setLastName(source.getUser().getLastName());
        map().getUserDto().setUsername(source.getUser().getUsername());
        map().getUserDto().setIsActive(source.getUser().getIsActive());
    }

}

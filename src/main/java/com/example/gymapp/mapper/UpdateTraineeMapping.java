package com.example.gymapp.mapper;

import com.example.gymapp.dto.trainee.TraineeRequestDTO;
import com.example.gymapp.model.Trainee;
import org.modelmapper.PropertyMap;

public class UpdateTraineeMapping extends PropertyMap<TraineeRequestDTO, Trainee> {
    @Override
    protected void configure() {
        map().getUser().setFirstName(source.getFirstName());
        map().getUser().setLastName(source.getLastName());
        map().getUser().setIsActive(source.getIsActive());
        map().setDateOfBirth(source.getDateOfBirth());
        map().setAddress(source.getAddress());
    }
}
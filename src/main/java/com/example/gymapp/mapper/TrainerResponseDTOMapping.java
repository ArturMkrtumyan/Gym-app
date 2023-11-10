package com.example.gymapp.mapper;


import com.example.gymapp.dto.trainer.TrainerResponseDTO;
import com.example.gymapp.model.Trainer;
import org.modelmapper.PropertyMap;

public class TrainerResponseDTOMapping extends PropertyMap<Trainer, TrainerResponseDTO> {
    @Override
    protected void configure() {
        map().setUsername(source.getUser().getUsername());
        map().setFirstName(source.getUser().getFirstName());
        map().setLastName(source.getUser().getLastName());
        map().setIsActive(source.getUser().getIsActive());
        map().setTrainees(source.getTrainees());
    }
}

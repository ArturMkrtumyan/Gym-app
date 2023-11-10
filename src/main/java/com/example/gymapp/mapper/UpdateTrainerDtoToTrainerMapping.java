package com.example.gymapp.mapper;
import com.example.gymapp.dto.trainer.UpdateTrainerDto;
import com.example.gymapp.model.Trainer;
import org.modelmapper.PropertyMap;

public class UpdateTrainerDtoToTrainerMapping extends PropertyMap<UpdateTrainerDto, Trainer> {

    @Override
    protected void configure() {
        map().getUser().setFirstName(source.getFirstName());
        map().getUser().setLastName(source.getLastName());
        map().getUser().setIsActive(source.getIsActive());
    }
}

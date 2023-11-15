package com.example.gymapp.mapper;
import com.example.gymapp.dto.training.TrainingResponseDTO;
import com.example.gymapp.model.Training;
import org.modelmapper.PropertyMap;

public class TrainingMapper extends PropertyMap<Training, TrainingResponseDTO> {
    @Override
    protected void configure() {
        map().setTrainingName(source.getTrainingName());
        map().setTrainingDate(source.getTrainingDate());
        map().setTrainingType(source.getTrainingType());
        map().setTrainingDuration(source.getTrainingDuration());
        map().setTrainerName(source.getTrainer().getUser().getFirstName());
        map().setTraineeName(source.getTrainee().getUser().getFirstName());
    }
}

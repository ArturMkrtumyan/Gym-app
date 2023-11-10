package com.example.gymapp.config;

import com.example.gymapp.mapper.TrainerResponseDTOMapping;
import com.example.gymapp.mapper.TrainingMapper;
import com.example.gymapp.mapper.UpdateTrainerDtoToTrainerMapping;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addMappings(new TrainerResponseDTOMapping());
        modelMapper.addMappings(new UpdateTrainerDtoToTrainerMapping());
        modelMapper.addMappings(new TrainingMapper());
        return modelMapper;
    }
}
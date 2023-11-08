package com.example.gymapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
public final class Trainer extends User {
    @ManyToOne
    @JoinColumn(name = "specialization", referencedColumnName = "id")
    private TrainingType specialization;
    @OneToMany(mappedBy = "trainer")
    private List<Training> trainingList;
    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees;
}
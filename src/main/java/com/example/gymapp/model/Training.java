package com.example.gymapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public  class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "trainee_id", referencedColumnName = "id")
    private Trainee trainee;
    @ManyToOne
    @JoinColumn(name = "trainer_id", referencedColumnName = "id")
    private Trainer trainer;
    @Column(nullable = false)
    private String trainingName;
    @ManyToOne
    @JoinColumn(name = "training_type", referencedColumnName = "id")
    private TrainingType trainingType;
    @Column(nullable = false)
    private LocalDate trainingDate;
    @Column
    private int trainingDuration;

}
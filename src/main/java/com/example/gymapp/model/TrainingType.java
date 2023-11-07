package com.example.gymapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String trainingTypeName;
    @JsonIgnore
    @OneToMany(mappedBy = "specialization")
    private List<Trainer> trainerList;
    @JsonIgnore
    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainingList;
}
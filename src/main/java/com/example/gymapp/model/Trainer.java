package com.example.gymapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"trainees"})
public class Trainer {
    @Id
    private Long id;
    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "specialization", referencedColumnName = "id")
    private TrainingType specialization;
    @OneToMany(mappedBy = "trainer")
    @JsonIgnore()
    private List<Training> trainingList;
    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees;
}
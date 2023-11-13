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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
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
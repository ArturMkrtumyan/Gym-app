package com.example.gymapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Entity
@Data
public final class Trainee extends User {
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    @Column
    private String address;
    @OneToMany(mappedBy = "trainee", cascade = CascadeType.REMOVE)
    private List<Training> trainingList;
    @ManyToMany()
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private Set<Trainer> trainers;
}
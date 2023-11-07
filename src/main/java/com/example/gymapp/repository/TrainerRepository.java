package com.example.gymapp.repository;


import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Trainer findTrainerById(Long id);

    @Query("SELECT t FROM Trainer t JOIN t.trainees trainee WHERE trainee.id = :traineeId")
    Set<Trainer> findTrainersByTraineeId(@Param("traineeId") Long traineeId);
    Optional<Trainer> findByUsername(String username);

    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.trainees WHERE t.username = :username")
    Trainer findTrainerProfileByUsername(@Param("username") String username);

    List<Training> findByTraineeUsernameAndTrainerFirstNameContainingAndTraineeFirstNameContaining(String username, String trainerName, String traineeName);
}

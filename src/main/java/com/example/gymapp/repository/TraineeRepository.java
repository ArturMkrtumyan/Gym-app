package com.example.gymapp.repository;

import com.example.gymapp.model.Trainee;
import com.example.gymapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Trainee findTraineeById(Long id);

    boolean existsByUsername(String username);

    Optional<Trainee> findByUsername(String username);

    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username")
    Trainee findTraineeProfileByUsername(@Param("username") String username);

    List<Training> findByTraineeUsernameAndTrainerFirstNameContainingAndTraineeFirstNameContaining(
            String username,
            String trainerName,
            String traineeName
    );

}

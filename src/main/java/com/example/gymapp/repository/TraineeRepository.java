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
    Optional<Trainee> findByUserUsername(String username);

    @Query("SELECT DISTINCT t FROM Trainee tr " +
            "JOIN tr.trainingList t " +
            "WHERE tr.id = :traineeId " +
            "AND (:periodFrom IS NULL OR t.trainingDate >= :periodFrom) " +
            "AND (:periodTo IS NULL OR t.trainingDate <= :periodTo) " +
            "AND (LOWER(:trainerName) IS NULL OR LOWER(t.trainer.user.firstName) = LOWER(:trainerName)) " +
            "AND (:trainingTypeId IS NULL OR t.trainingType.id = :trainingTypeId)")
    List<Training> findTrainingsByTraineeWithFiltering(
            @Param("traineeId") Long traineeId,
            @Param("periodFrom") LocalDate periodFrom,
            @Param("periodTo") LocalDate periodTo,
            @Param("trainerName") String trainerName,
            @Param("trainingTypeId") Long trainingTypeId
    );

}

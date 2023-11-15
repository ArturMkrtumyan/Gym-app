package com.example.gymapp.repository;


import com.example.gymapp.model.Trainer;
import com.example.gymapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Trainer findTrainerById(Long id);

    @Query("SELECT t FROM Trainer t JOIN t.trainees trainee WHERE trainee.id = :traineeId")
    Set<Trainer> findTrainersByTraineeId(@Param("traineeId") Long traineeId);

    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.trainees WHERE t.user.username = :username")
    Trainer findTrainerProfileByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT t FROM Trainer tr " +
            "JOIN tr.trainingList t " +
            "WHERE tr.id = :trainerId " +
            "AND (:periodFrom IS NULL OR t.trainingDate >= :periodFrom) " +
            "AND (:periodTo IS NULL OR t.trainingDate <= :periodTo) " +
            "AND (LOWER(:traineeName) IS NULL OR LOWER(t.trainee.user.firstName) = LOWER(:traineeName)) " +
            "AND (:trainingTypeId IS NULL OR t.trainingType.id = :trainingTypeId)")
    List<Training> findTrainingsByTrainerWithFiltering(
            @Param("trainerId") Long trainerId,
            @Param("periodFrom") LocalDate periodFrom,
            @Param("periodTo") LocalDate periodTo,
            @Param("traineeName") String traineeName,
            @Param("trainingTypeId") Long trainingTypeId
    );


}

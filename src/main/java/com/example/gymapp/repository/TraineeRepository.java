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


//    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers tr " +
//            "WHERE t.user.username = :username " +
//            "AND tr.specialization.typeName LIKE %:specializationName% " +
//            "AND t.user.firstName LIKE %:traineeName%")
//    List<Training> findByUserUsernameAndTrainers_Specialization_TypeNameContainingAndUser_FirstNameContaining(
//            @Param("username") String username,
//            @Param("specializationName") String specializationName,
//            @Param("traineeName") String traineeName
//    );


}

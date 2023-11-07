package com.example.gymapp.repository;

import com.example.gymapp.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
}

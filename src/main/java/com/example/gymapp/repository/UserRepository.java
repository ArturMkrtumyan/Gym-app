package com.example.gymapp.repository;

import com.example.gymapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT COALESCE(MAX(CASE WHEN u.username = :newUsername THEN 1 ELSE 0 END), 0) " +
            "FROM User u")
    Optional<Integer> findMaxUsernameOccurrence(@Param("newUsername") String newUsername);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
    void updatePasswordByUsername(String username, String newPassword);

    boolean existsByUsername(String username);
}

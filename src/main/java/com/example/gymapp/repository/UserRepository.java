package com.example.gymapp.repository;

import com.example.gymapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u.username FROM User u WHERE u.username LIKE :newUsername%")
    List<String> findUsernameOccurencies(String newUsername);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
    void updatePasswordByUsername(String username, String newPassword);

    boolean existsByUsername(String username);
}

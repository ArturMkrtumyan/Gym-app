package com.example.gymapp.service;


import com.example.gymapp.exception.UserNotFoundException;
import com.example.gymapp.model.User;
import com.example.gymapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private static final int PASSWORD_LENGTH = 10;

    public String generateUsername(String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            String clearedFirstName = clearString(firstName);
            String clearedLastName = clearString(lastName);
            String newUsername = clearedFirstName + "." + clearedLastName;

            int maxOccurrence = userRepository.findMaxUsernameOccurrence(newUsername).orElse(0);

            return maxOccurrence == 0 ? newUsername : newUsername + (maxOccurrence + 1);
        } else {
            return "";
        }
    }

    public String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);
    }

    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        user.setPassword(newPassword);
        userRepository.save(user);

        return true;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private static String clearString(String s) {
        return s.trim().toLowerCase()
                .replace(" ", "")
                .replaceAll("\\d", "");
    }
}

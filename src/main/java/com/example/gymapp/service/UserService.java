package com.example.gymapp.service;


import com.example.gymapp.model.User;
import com.example.gymapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;


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

            List<String> usernames = userRepository.findUsernameOccurencies(newUsername);
            OptionalInt max = usernames.stream()
                    .map(s -> s.replace(newUsername, ""))
                    .mapToInt(s -> s.isEmpty() ? 0 : Integer.parseInt(s))
                    .max();

            return max.isEmpty() ? newUsername : newUsername + (max.getAsInt() + 1);
        } else {
            return "";
        }
    }

    public String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);
    }

    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        try {
            // You can find the User by username and then update the password.
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                user.setPassword(newPassword);
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
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

package com.example.gymapp.exception;

public class NoSuchTraineeExistException extends RuntimeException {
    public NoSuchTraineeExistException(String message) {
        super(message);
    }
}

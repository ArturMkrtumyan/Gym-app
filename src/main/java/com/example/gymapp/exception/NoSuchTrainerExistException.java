package com.example.gymapp.exception;

public class NoSuchTrainerExistException extends RuntimeException {
    public NoSuchTrainerExistException(String message) {
        super(message);
    }
}

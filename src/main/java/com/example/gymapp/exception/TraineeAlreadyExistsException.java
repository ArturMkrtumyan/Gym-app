package com.example.gymapp.exception;

public class TraineeAlreadyExistsException extends RuntimeException{
    public TraineeAlreadyExistsException(String message) {
        super(message);
    }
}

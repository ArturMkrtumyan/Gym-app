package com.example.gymapp.exception;

public class TrainerNotFoundException extends RuntimeException{

    public TrainerNotFoundException(String message) {
        super(message);
    }
}

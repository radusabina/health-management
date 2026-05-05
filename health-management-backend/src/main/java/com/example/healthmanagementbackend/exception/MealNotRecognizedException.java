package com.example.healthmanagementbackend.exception;

public class MealNotRecognizedException extends RuntimeException {
    public MealNotRecognizedException(String message) {
        super(message);
    }
}

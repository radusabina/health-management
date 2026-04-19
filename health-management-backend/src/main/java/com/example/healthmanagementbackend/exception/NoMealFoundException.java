package com.example.healthmanagementbackend.exception;

public class NoMealFoundException extends RuntimeException {
    public NoMealFoundException(String message) {
        super(message);
    }
}

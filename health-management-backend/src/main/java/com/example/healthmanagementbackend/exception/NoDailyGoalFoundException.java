package com.example.healthmanagementbackend.exception;

public class NoDailyGoalFoundException extends RuntimeException {
    public NoDailyGoalFoundException(String message) {
        super(message);
    }
}

package com.example.healthmanagementbackend.handler;

import com.example.healthmanagementbackend.exception.InvalidCredentialsException;
import com.example.healthmanagementbackend.exception.NoDailyGoalFoundException;
import com.example.healthmanagementbackend.exception.NoGeneralGoalFoundException;
import com.example.healthmanagementbackend.exception.NoMealFoundException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ────────────────────────────────────────────────────────
    @ExceptionHandler({
        NoUserFoundException.class,
        NoMealFoundException.class,
        NoDailyGoalFoundException.class,
        NoGeneralGoalFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return body(HttpStatus.NOT_FOUND, e);
    }

    // ── 409 Conflict ─────────────────────────────────────────────────────────
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleConflict(UserAlreadyExistsException e) {
        return body(HttpStatus.CONFLICT, e);
    }

    // ── 401 Unauthorized ─────────────────────────────────────────────────────
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(InvalidCredentialsException e) {
        return body(HttpStatus.UNAUTHORIZED, e);
    }

    // ── 400 Bad Request ──────────────────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return body(HttpStatus.BAD_REQUEST, e);
    }

    // ── 500 Internal Server Error (fallback) ─────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception e) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private static ResponseEntity<Map<String, String>> body(HttpStatus status, Exception e) {
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "type", e.getClass().getSimpleName(),
                        "message", e.getMessage() != null ? e.getMessage() : "Unexpected error"
                ));
    }
}

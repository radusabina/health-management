package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String email, String password, String fullName, int heightCm, String gender, int age) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .heightCm(heightCm)
                .gender(gender)
                .age(age)
                .createdAt(LocalDateTime.now()).build();

        userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("User not found"));
    }

    public void updateUser(UUID userId, String email, String password, String fullName, int heightCm, String gender, int age) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("User not found"));

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setHeightCm(heightCm);
        user.setGender(gender);
        user.setAge(age);

        userRepository.save(user);
        LOGGER.info("User with id " + userId + " updated");
    }

    public boolean isPasswordValid(String password) {
        return passwordEncoder.matches(password, passwordEncoder.encode(password));
    }

    public boolean deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("User not found"));
        userRepository.delete(user);
        return true;
    }


}
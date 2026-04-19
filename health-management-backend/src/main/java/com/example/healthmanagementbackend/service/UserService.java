package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.exception.InvalidCredentialsException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.exception.UserAlreadyExistsException;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.model.enums.Gender;
import com.example.healthmanagementbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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

    public void register(String email, String password, String fullName, int heightCm, int weightKg, Gender gender, int age) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new UserAlreadyExistsException("An user with this email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .heightCm(heightCm)
                .weightKg(weightKg)
                .gender(gender)
                .age(age)
                .createdAt(LocalDateTime.now()).build();

        userRepository.save(user);
    }

    public User login(String email, String password) throws InvalidCredentialsException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        LOGGER.info("Logged in user: " + user.getEmail());
        return user;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("User not found"));
    }

    public void updateUser(UUID userId, String email, String password, String fullName, int heightCm, int weightKg, Gender gender, int age) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("User not found"));

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setHeightCm(heightCm);
        user.setWeightKg(weightKg);
        user.setGender(gender);
        user.setAge(age);

        userRepository.save(user);
        LOGGER.info("User with id " + userId + " updated");
    }

    public boolean isPasswordValid(String password) {
        return passwordEncoder.matches(password, passwordEncoder.encode(password));
    }

    public boolean deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            LOGGER.info("User not found");
            return false;
        }
        userRepository.delete(user);
        LOGGER.info("User with id " + userId + " deleted");
        return true;
    }


}
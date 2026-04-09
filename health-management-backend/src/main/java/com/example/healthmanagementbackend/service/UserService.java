package com.example.healthmanagementbackend.service;

import com.example.healthmanagement.dto.LoginResponse;
import com.example.healthmanagement.exception.InvalidCredentialsException;
import com.example.healthmanagement.model.User;
import com.example.healthmanagement.repository.UserRepository;
import com.example.healthmanagement.service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(String email, String password, String fullName, int heightCm, String gender, int age) {
        User user = User.builder().email(email).password(passwordEncoder.encode(password)).fullName(fullName).heightCm(heightCm).gender(gender).age(age).createdAt(LocalDateTime.now()).build();

        userRepository.save(user);
    }

    public LoginResponse login(String email, String password) throws InvalidCredentialsException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new LoginResponse(user.getId(), token);
    }
}
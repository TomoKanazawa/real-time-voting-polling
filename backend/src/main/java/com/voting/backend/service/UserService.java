package com.voting.backend.service;

import com.voting.backend.model.User;
import com.voting.backend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password) {
        // Log for debugging
        System.out.println("Registering user: " + username + ", " + email);
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        try {
            if (userRepository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("USER");
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            System.out.println("User registered successfully: " + savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public User authenticateUser(String username, String password) {
        // Log for debugging
        System.out.println("Authenticating user: " + username);
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("Invalid password");
            }
            
            System.out.println("User authenticated successfully: " + user.getId());
            return user;
        } catch (Exception e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String generateToken(User user) {
        // Log for debugging
        System.out.println("Generating token for user: " + user.getId());
        
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);
            
            // Use HS256 instead of HS512 for simpler key requirements
            String token = Jwts.builder()
                    .setSubject(user.getId().toString())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .claim("username", user.getUsername())
                    .claim("role", user.getRole())
                    .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                    .compact();
            
            System.out.println("Token generated successfully");
            return token;
        } catch (Exception e) {
            System.err.println("Error generating token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 
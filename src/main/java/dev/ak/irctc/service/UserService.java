package dev.ak.irctc.service;

import dev.ak.irctc.dto.RegisterRequest;
import dev.ak.irctc.entity.User;
import dev.ak.irctc.enums.Role;
import dev.ak.irctc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerNewUser(RegisterRequest request) {
        // 1. Check for duplicates
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // 2. Create the new User entity
        User newUser = new User();
        newUser.setFullName(request.fullName());
        newUser.setEmail(request.email());
        newUser.setAge(request.age());
        newUser.setGender(request.gender());
        newUser.setDateOfBirth(request.dateOfBirth());
        newUser.setCountry(request.country());
        newUser.setEmail(request.email());
        
        // 3. Hash the password using BCrypt
        newUser.setPassword(passwordEncoder.encode(request.password()));
        
        // 4. Set default role
        newUser.setRole(Role.USER);

        // 5. Save to PostgreSQL
        userRepository.save(newUser);
    }
}
package com.astro.theRealAstrologers.Controller;

import com.astro.theRealAstrologers.DTO.LoginRequest;
import com.astro.theRealAstrologers.Entity.User;
import com.astro.theRealAstrologers.Repository.UserRepository;
import com.astro.theRealAstrologers.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {

        // 1. Ensure the email isn't already taken
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Error: This email is already registered!";
        }

        // 2. The Golden Rule of Backend: NEVER save plain text passwords
        String plainTextPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(plainTextPassword);

        // Replace the plain text with the scrambled hash
        user.setPassword(hashedPassword);

        // 3. Save the secure user to PostgreSQL
        userRepository.save(user);

        return "Success! " + user.getFullName() + " has been registered safely.";
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {

        // 1. Check if the user exists in the database
        User user = userRepository.findByEmail(loginRequest.email)
                .orElse(null);

        if (user == null) {
            return "Error: User not found!";
        }

        // 2. Use the Encoder to verify the typed password matches the Hash in the DB
        if (passwordEncoder.matches(loginRequest.password, user.getPassword())) {

            // 3. Success! Generate and return the JWT VIP Pass
            return jwtUtil.generateToken(user.getEmail());

        } else {
            return "Error: Incorrect password!";
        }
    }
}

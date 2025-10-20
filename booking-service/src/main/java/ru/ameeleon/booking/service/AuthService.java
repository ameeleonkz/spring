package ru.ameeleon.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ameeleon.booking.dto.LoginRequest;
import ru.ameeleon.booking.dto.RegisterRequest;
import ru.ameeleon.booking.entity.User;
import ru.ameeleon.booking.repository.UserRepository;
import ru.ameeleon.booking.security.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Если роль передана, используем её, иначе USER
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.USER);

        userRepository.save(user);

        return jwtUtil.generateToken(user.getUsername());
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}
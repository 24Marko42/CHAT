package org.example.tetetete.server;

import org.example.tetetete.common.exception.InvalidCredentialsException;
import org.example.tetetete.common.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository = new UserRepository();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void register(String username, String password) throws UserAlreadyExistsException {
        if (userRepository.exists(username)) {
            throw new UserAlreadyExistsException("User with username " + username + " already exists.");
        }
        String hashedPassword = passwordEncoder.encode(password);
        userRepository.addUser(username, hashedPassword);
        logger.info("User registered: {}", username);
    }

    public void authenticate(String username, String password) throws InvalidCredentialsException {
        if (!userRepository.exists(username) || !passwordEncoder.matches(password, userRepository.getHashedPassword(username))) {
            throw new InvalidCredentialsException("Invalid credentials for user: " + username);
        }
        logger.info("User authenticated: {}", username);
    }

    private String getHashedPassword(String username) {
        return userRepository.getHashedPassword(username);
    }
}

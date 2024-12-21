package org.example.tetetete.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final String USERS_FILE = "users.json";
    private final Map<String, String> users = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserRepository() {
        loadUsers();
    }

    private void loadUsers() {
        try {
            File file = new File(USERS_FILE);
            if (file.exists()) {
                users.putAll(objectMapper.readValue(file, new TypeReference<Map<String, String>>() {}));
            }
        } catch (IOException e) {
            logger.error("Error loading users from file", e);
        }
    }

    public void saveUsers() {
        try {
            objectMapper.writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            logger.error("Error saving users to file", e);
        }
    }

    public void addUser(String username, String hashedPassword) {
        users.put(username, hashedPassword);
        saveUsers();
    }

    public boolean exists(String username) {
        return users.containsKey(username);
    }

    public String getHashedPassword(String username) {
        return users.get(username);
    }
}

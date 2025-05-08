package com.example.omegleclone.service;

import com.example.omegleclone.model.User;
import com.example.omegleclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Set initial values
        user.setLastLogin(LocalDateTime.now());

        // Save the user
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error registering user: " + e.getMessage());
        }
    }

    @Override
    public User findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateLastLogin(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Invalid user");
        }

        // Verify user exists
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login time
        existingUser.setLastLogin(LocalDateTime.now());

        try {
            return userRepository.save(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }
}

//package com.example.omegleclone.service;
//
//import com.example.omegleclone.model.User;
//import com.example.omegleclone.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.retry.annotation.Backoff;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    @Transactional
//    @Retryable(value = {DataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
//    public User registerUser(User user) {
//        if (userRepository.findByUsername(user.getUsername()) != null) {
//            throw new RuntimeException("Username already exists");
//        }
//
//        user.setLastLogin(LocalDateTime.now());
//
//        try {
//            return userRepository.save(user);
//        } catch (Exception e) {
//            throw new RuntimeException("Error registering user: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public User findUserByUsername(String username) {
//        if (username == null || username.trim().isEmpty()) {
//            throw new IllegalArgumentException("Username cannot be empty");
//        }
//        return userRepository.findByUsername(username);
//    }
//
//    @Override
//    @Transactional
//    @Retryable(value = {DataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
//    public User updateLastLogin(User user) {
//        if (user == null || user.getId() == null) {
//            throw new IllegalArgumentException("Invalid user");
//        }
//
//        User existingUser = userRepository.findById(user.getId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        existingUser.setLastLogin(LocalDateTime.now());
//
//        try {
//            return userRepository.save(existingUser);
//        } catch (Exception e) {
//            throw new RuntimeException("Error updating user: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public List<User> findUsersByInterests(List<String> interests) {
//        if (interests == null || interests.isEmpty()) {
//            throw new IllegalArgumentException("Interests list cannot be empty");
//        }
//
//        // Convert the list of interests to a regex pattern
//        String interestsPattern = interests.stream()
//                .map(interest -> "\\b" + interest + "\\b")
//                .collect(Collectors.joining("|"));
//
//        return userRepository.findByInterests(interestsPattern);
//    }
//
//    @Override
//    public List<User> findAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    @Transactional
//    public int migrateInterestsToJoinTable() {
//        List<User> usersWithInterests = userRepository.findAll().stream()
//                .filter(user -> user.getInterests() != null && !user.getInterests().trim().isEmpty())
//                .collect(Collectors.toList());
//
//        int totalMigrated = 0;
//
//        for (User user : usersWithInterests) {
//            String[] interests = user.getInterests().split(",");
//            for (String interest : interests) {
//                String trimmedInterest = interest.trim();
//                if (!trimmedInterest.isEmpty()) {
//                    userRepository.addUserInterest(user.getId(), trimmedInterest);
//                    totalMigrated++;
//                }
//            }
//        }
//
//        return totalMigrated;
//    }
//}
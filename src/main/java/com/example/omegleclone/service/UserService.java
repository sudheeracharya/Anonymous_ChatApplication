package com.example.omegleclone.service;

import com.example.omegleclone.model.User;

/**
 * Interface for user service operations
 */
public interface UserService {
    /**
     * Register a new user
     * @param user the user to register
     * @return the registered user
     */
    User registerUser(User user);

    /**
     * Find a user by their username
     * @param username the username to search for
     * @return the found user or null
     */
    User findUserByUsername(String username);

    /**
     * Update the last login time for a user
     * @param user the user to update
     * @return the updated user
     */
    User updateLastLogin(User user);
}
//
//package com.example.omegleclone.service;
//
//import com.example.omegleclone.model.User;
//import java.util.List;
//
//public interface UserService {
//    User registerUser(User user);
//    User findUserByUsername(String username);
//    User updateLastLogin(User user);
//    List<User> findUsersByInterests(List<String> interests);
//    List<User> findAllUsers();
//}

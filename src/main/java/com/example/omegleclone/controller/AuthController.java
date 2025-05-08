package com.example.omegleclone.controller;

import com.example.omegleclone.model.User;
import com.example.omegleclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            User existingUser = userService.findUserByUsername(user.getUsername());

            if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
                existingUser = userService.updateLastLogin(existingUser);
                String mockToken = "mock-jwt-token-" + existingUser.getUsername();

                response.put("success", true);
                response.put("token", mockToken);
                response.put("user", existingUser);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid username or password");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            User registeredUser = userService.registerUser(user);
            response.put("success", true);
            response.put("message", "Registration successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String mockToken = token.substring(7);
            if (mockToken.startsWith("mock-jwt-token-")) {
                String username = mockToken.substring("mock-jwt-token-".length());
                User user = userService.findUserByUsername(username);
                if (user != null) {
                    return ResponseEntity.ok(user);
                }
            }
        }
        return ResponseEntity.status(401).build();
    }
}
//
//
//
//



//package com.example.omegleclone.controller;
//
//import com.example.omegleclone.model.User;
//import com.example.omegleclone.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            User existingUser = userService.findUserByUsername(user.getUsername());
//
//            if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
//                existingUser = userService.updateLastLogin(existingUser);
//                String mockToken = "mock-jwt-token-" + existingUser.getUsername();
//
//                response.put("success", true);
//                response.put("token", mockToken);
//                response.put("user", existingUser);
//                return ResponseEntity.ok(response);
//            } else {
//                response.put("success", false);
//                response.put("message", "Invalid username or password");
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            User registeredUser = userService.registerUser(user);
//            response.put("success", true);
//            response.put("message", "Registration successful");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @GetMapping("/user")
//    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
//        if (token != null && token.startsWith("Bearer ")) {
//            String mockToken = token.substring(7);
//            if (mockToken.startsWith("mock-jwt-token-")) {
//                String username = mockToken.substring("mock-jwt-token-".length());
//                User user = userService.findUserByUsername(username);
//                if (user != null) {
//                    return ResponseEntity.ok(user);
//                }
//            }
//        }
//        return ResponseEntity.status(401).build();
//    }
//}























//
//package com.example.omegleclone.controller;
//
//import com.example.omegleclone.model.User;
//import com.example.omegleclone.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            User existingUser = userService.findUserByUsername(user.getUsername());
//
//            if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
//                existingUser = userService.updateLastLogin(existingUser);
//                String mockToken = "mock-jwt-token-" + existingUser.getUsername();
//
//                response.put("success", true);
//                response.put("token", mockToken);
//                response.put("user", existingUser);
//                return ResponseEntity.ok(response);
//            } else {
//                response.put("success", false);
//                response.put("message", "Invalid username or password");
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            User registeredUser = userService.registerUser(user);
//            response.put("success", true);
//            response.put("message", "Registration successful");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @GetMapping("/user")
//    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
//        if (token != null && token.startsWith("Bearer ")) {
//            String mockToken = token.substring(7);
//            if (mockToken.startsWith("mock-jwt-token-")) {
//                String username = mockToken.substring("mock-jwt-token-".length());
//                User user = userService.findUserByUsername(username);
//                if (user != null) {
//                    return ResponseEntity.ok(user);
//                }
//            }
//        }
//        return ResponseEntity.status(401).build();
//    }
//
//    @GetMapping("/users/by-interests")
//    public ResponseEntity<Map<String, Object>> getUsersByInterests(@RequestParam List<String> interests) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            List<User> users = userService.findUsersByInterests(interests);
//            response.put("success", true);
//            response.put("users", users);
//            response.put("count", users.size());
//            response.put("interests", interests);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/migrate-interests")
//    public ResponseEntity<Map<String, Object>> migrateInterests() {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            int count = userService.migrateInterestsToJoinTable();
//            response.put("success", true);
//            response.put("migratedCount", count);
//            response.put("message", "Successfully migrated " + count + " interests to join table");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//}
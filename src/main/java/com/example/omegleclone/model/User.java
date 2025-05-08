package com.example.omegleclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String country;
    private String interests;
    private LocalDateTime lastLogin;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}



//
//package com.example.omegleclone.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Set;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "users")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    private String email;
//    private String firstName;
//    private String lastName;
//    private LocalDate dateOfBirth;
//
//    @Enumerated(EnumType.STRING)
//    private Gender gender;
//
//    private String country;
//
//    @ElementCollection
//    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
//    @Column(name = "interest")
//    private Set<String> interests;
//
//    private LocalDateTime lastLogin;
//
//    public enum Gender {
//        MALE, FEMALE, OTHER
//    }
//}
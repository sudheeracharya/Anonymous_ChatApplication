package com.example.omegleclone.repository;

import com.example.omegleclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by their username
     * @param username the username to search for
     * @return the found user or null
     */
    User findByUsername(String username);
}


//
//package com.example.omegleclone.repository;
//
//import com.example.omegleclone.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//    User findByUsername(String username);
//
//    @Query(value = "SELECT DISTINCT u.* FROM users u " +
//            "WHERE u.interests REGEXP :interests",
//            nativeQuery = true)
//    List<User> findByInterests(@Param("interests") String interests);
//}
package com.pinkproject.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("""
            SELECT u
            FROM User u
            WHERE u.email = :email
            """)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.email = :email AND u.password = :password
            """)
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}

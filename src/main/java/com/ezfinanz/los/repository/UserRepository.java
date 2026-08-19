package com.ezfinanz.los.repository;

import com.ezfinanz.los.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA magic: It will automatically write the SQL to find a user by
    // email!
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}

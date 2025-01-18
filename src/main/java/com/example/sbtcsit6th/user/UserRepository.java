package com.example.sbtcsit6th.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional<User> findByUsernameAndPassword(String username, String password);

	boolean existsBySession(String session);

	Optional<User> findBySession(String session);

	Optional<User> findByUsername(String username);

}

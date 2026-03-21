package com.pebble.baseAuth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

}

package com.pebble.baseAuth.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {

    private final Long id;
    private final String username;
    private final String password;
    private final String provider;
    private final String providerId;
    private final UserRole role;
    private LocalDateTime deletedAt;

    public User(String username, String password) {
        this(null, username, password, null, null, UserRole.ROLE_USER, null);
    }

    public User(String username, String password, UserRole role) {
        this(null, username, password, null, null, role, null);
    }

    public User(Long id, String username, String password, String provider, String providerId, UserRole role, LocalDateTime deletedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.role = (role != null) ? role : UserRole.ROLE_USER;
        this.deletedAt = deletedAt;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}

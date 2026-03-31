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

    /**
     * 소셜 로그인을 통한 신규 사용자 생성 (정적 팩토리 메서드)
     */
    public static User createSocialUser(String username, String provider, String providerId) {
        return new User(
                null,
                username,
                null, // 소셜 사용자는 비밀번호 없음
                provider,
                providerId,
                UserRole.ROLE_USER,
                null
        );
    }
}

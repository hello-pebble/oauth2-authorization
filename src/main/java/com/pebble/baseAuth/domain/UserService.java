package com.pebble.baseAuth.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(String username, String password) {

        if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            throw new UserException("이미 존재하는 사용자명입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        return userRepository.save(new User(username, encodedPassword, UserRole.ROLE_USER));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 소셜 사용자 정보 저장 또는 업데이트 (DB 트랜잭션 범위 최소화)
     */
    @Transactional
    public User saveOrUpdateSocialUser(String provider, String providerId, String email, String name) {
        return userRepository.findByProviderAndProviderIdAndDeletedAtIsNull(provider, providerId)
                .map(existingUser -> {
                    // 필요한 경우 정보 업데이트 로직 추가 (예: 이름 변경 등)
                    return existingUser;
                })
                .orElseGet(() -> {
                    String username = (email != null && !email.isEmpty()) ? email : provider + "_" + providerId;
                    
                    if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
                        username = username + "_" + java.util.UUID.randomUUID().toString().substring(0, 5);
                    }

                    return userRepository.save(User.createSocialUser(username, provider, providerId));
                });
    }

}

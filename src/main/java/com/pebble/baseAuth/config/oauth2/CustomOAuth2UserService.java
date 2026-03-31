package com.pebble.baseAuth.config.oauth2;

import com.pebble.baseAuth.domain.User;
import com.pebble.baseAuth.domain.UserRepository;
import com.pebble.baseAuth.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

        User user = saveOrUpdate(oAuth2UserInfo, registrationId);

        return new CustomOAuth2User(user, attributes);
    }

    private User saveOrUpdate(OAuth2UserInfo oAuth2UserInfo, String provider) {
        return userRepository.findByProviderAndProviderIdAndDeletedAtIsNull(provider, oAuth2UserInfo.getId())
                .map(existingUser -> {
                    // 필요한 경우 정보 업데이트 로직 추가
                    return existingUser;
                })
                .orElseGet(() -> {
                    // 소셜 로그인을 통한 첫 가입 시 사용자 생성
                    String username = oAuth2UserInfo.getEmail();
                    if (username == null || username.isEmpty()) {
                        username = provider + "_" + oAuth2UserInfo.getId();
                    }
                    
                    // 만약 중복된 username이 있다면 랜덤 값을 붙여서 생성
                    if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
                        username = username + "_" + UUID.randomUUID().toString().substring(0, 5);
                    }

                    User newUser = new User(
                            null,
                            username,
                            null, // 소셜 로그인은 비밀번호 없음
                            provider,
                            oAuth2UserInfo.getId(),
                            UserRole.ROLE_USER,
                            null
                    );
                    return userRepository.save(newUser);
                });
    }
}

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

    private final com.pebble.baseAuth.domain.UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // [CTO 리뷰 반영] 외부 IdP API 호출 시 DB 트랜잭션을 잡지 않도록 수정
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

        // DB 작업만 트랜잭션 범위 내에서 수행되도록 서비스에 위임
        User user = userService.saveOrUpdateSocialUser(
                registrationId, 
                oAuth2UserInfo.getId(), 
                oAuth2UserInfo.getEmail(), 
                oAuth2UserInfo.getName()
        );

        return new CustomOAuth2User(user, attributes);
    }
}

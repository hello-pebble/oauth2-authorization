package com.pebble.baseAuth.config.oauth2;

import com.pebble.baseAuth.config.JwtProvider;
import com.pebble.baseAuth.persistence.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getUser().getUsername();
        String role = oAuth2User.getUser().getRole().name();

        String accessToken = jwtProvider.createAccessToken(username, role);
        String refreshToken = jwtProvider.createRefreshToken(username);

        // Redis에 Refresh Token 저장
        refreshTokenRepository.save(username, refreshToken, jwtProvider.getRefreshExpiration());

        // [CTO 리뷰 반영] 쿠키 만료 시간을 JwtProvider 설정과 동기화
        addCookie(response, "accessToken", accessToken, (int) (jwtProvider.getAccessExpiration() / 1000));
        addCookie(response, "refreshToken", refreshToken, (int) (jwtProvider.getRefreshExpiration() / 1000));

        // 메인 페이지로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, "/");
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS가 아니면 로컬 테스트 시 주의 필요
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}

package vn.dungjava.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import vn.dungjava.common.Gender;
import vn.dungjava.common.UserStatus;
import vn.dungjava.model.User;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.JwtService;
import vn.dungjava.service.RefreshTokenStore;

import java.io.IOException;
import java.time.Duration;

@Component
@AllArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenStore refreshTokenStore;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();

        String email = null;
        String name = null;

        if(principal instanceof OidcUser){
            email = ((OidcUser) principal).getEmail();
            name = ((OidcUser) principal).getName();
        } else if(principal instanceof OAuth2User){
            email = ((OAuth2User) principal).getAttribute("email");
            name = ((OAuth2User) principal).getAttribute("name");
        }

        if(email == null){
            response.sendError(401, "Cannot get email from Google");
            return;
        }

        User user = userRepository.getByEmail(email);

        if(user == null){
            user = new User();
            user.setEmail(email);
            user.setStatus(UserStatus.ACTIVE);

        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            user.setUsername(email);
        }

        user = userRepository.save(user);

        var authorities = authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        String accessToken = jwtService.generateAccessToken(user.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), authorities);

        refreshTokenStore.store(refreshToken, user.getUsername());

        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofDays(30))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        //TODO: sau khi co FE
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"accessToken\":\"" + accessToken + "\"}");
    }
}

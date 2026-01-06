package vn.dungjava.controller;

import com.sendgrid.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dungjava.controller.request.SignInRequest;
import vn.dungjava.controller.response.AuthTokens;
import vn.dungjava.controller.response.TokenResponse;
import vn.dungjava.service.AuthenticationService;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTHENTICAION-CONTROLLER")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private static final boolean COOKIE_SECURE = false;
    private static final Duration COOKIE_EXPIRATION = Duration.ofDays(30);

    @Operation(summary = "Access token", description = "Get access token and refresh token by username and password")
    @PostMapping("/access-token")
    public ResponseEntity<TokenResponse> getAccessToken(@RequestBody SignInRequest request){
        log.info("Access token request");
        AuthTokens authTokens = authenticationService.getAccessToken(request);

        ResponseCookie refreshCookie = buildRefreshCookie(authTokens.getRefreshToken(), COOKIE_EXPIRATION);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(TokenResponse.builder().accessToken(authTokens.getAccessToken()).build());
    }

    @Operation(summary = "Refresh token", description = "Get new access token by refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> getRefreshToken(HttpServletRequest request) {
        log.info("Refresh token request");

        AuthTokens tokens = authenticationService.getRefreshToken(request);

        ResponseCookie refreshToken = buildRefreshCookie(tokens.getRefreshToken(), COOKIE_EXPIRATION);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                .body(TokenResponse.builder().accessToken(tokens.getAccessToken()).build());
    }

    @Operation(summary = "Logout", description = "Logout")
    @PostMapping("/logout")
    public ResponseEntity<TokenResponse> logout(HttpServletRequest request) {
        log.info("Logout request");

        authenticationService.logout(request);

        ResponseCookie clearCookie = buildRefreshCookie("", COOKIE_EXPIRATION);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }

    private ResponseCookie buildRefreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from("refresh-token", value)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(maxAge)
                .build();
    }
}

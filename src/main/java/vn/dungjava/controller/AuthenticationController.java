package vn.dungjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dungjava.controller.request.SignInRequest;
import vn.dungjava.controller.response.TokenResponse;
import vn.dungjava.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTHENTICAION-CONTROLLER")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Access token", description = "Get access token and refresh token by username and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody SignInRequest request){
        log.info("Access token request");
        return authenticationService.getAccessToken(request);
    }

    @Operation(summary = "Refresh token", description = "Get new access token by refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Refresh token request");
        return authenticationService.getRefreshToken(refreshToken);
    }
}

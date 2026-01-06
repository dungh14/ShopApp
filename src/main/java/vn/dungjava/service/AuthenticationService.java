package vn.dungjava.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.dungjava.controller.request.SignInRequest;
import vn.dungjava.controller.response.AuthTokens;
import vn.dungjava.controller.response.TokenResponse;

public interface AuthenticationService {
    AuthTokens getAccessToken(SignInRequest request);
    AuthTokens getRefreshToken(HttpServletRequest request);
    void logout(HttpServletRequest request);
}

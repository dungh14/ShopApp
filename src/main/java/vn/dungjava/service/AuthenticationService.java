package vn.dungjava.service;

import vn.dungjava.controller.request.SignInRequest;
import vn.dungjava.controller.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(String request);
}

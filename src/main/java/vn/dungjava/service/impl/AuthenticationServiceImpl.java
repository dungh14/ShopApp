package vn.dungjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.dungjava.common.TokenType;
import vn.dungjava.controller.request.SignInRequest;
import vn.dungjava.controller.response.TokenResponse;
import vn.dungjava.exception.InvalidDataException;
import vn.dungjava.model.User;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.AuthenticationService;
import vn.dungjava.service.JwtService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "AUTHENTICATION-SERVICE")
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("getAccessToken");

        List<String> authorities = new ArrayList<>();
        try {
            //thuc hien xac thuc voi username va password
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("Authenticated: {}", authentication.isAuthenticated());
            log.info("User: {}", authentication.getAuthorities());
            authorities.add(authentication.getAuthorities().toString());

            //xac thuc thanh cong thi luu vao security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Login failed!, message: {}",e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }
        String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(request.getUsername(), authorities);

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("getRefreshToken");

        if(!StringUtils.isEmpty(refreshToken)) {
            throw new InvalidDataException("Token is empty");
        }
        try {
            //Verify token
            String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

            //check user is active or inactivated
            User user = userRepository.findByUsername(username);
            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.toString()));

            //generate new access token
            String accessToken = jwtService.generateAccessToken(user.getUsername(), authorities);

            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (Exception e) {
            log.error("Login failed!, message: {}",e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }
    }
}

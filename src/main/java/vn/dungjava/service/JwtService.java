package vn.dungjava.service;

import vn.dungjava.common.TokenType;

import java.util.List;

public interface JwtService {
    String generateAccessToken(String username, List<String> authorities);

    String generateRefreshToken(String username, List<String> authorities);

    String extractUsername(String token, TokenType type);
}

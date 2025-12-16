package vn.dungjava.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vn.dungjava.common.TokenType;
import vn.dungjava.exception.InvalidDataException;
import vn.dungjava.service.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryDay}")
    private Long expiryDay;

    @Value("${jwt.expiryMinutes}")
    private Long expiryMinutes;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String generateAccessToken(String username, List<String> authorities) {
        log.info("generateAccessToken username: {} with authorities: {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);

        return generateToken(claims, username);
    }

    @Override
    public String generateRefreshToken(String username, List<String> authorities) {
        log.info("generateRefreshToken username: {} with authorities: {}", username, authorities);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);

        return generateRefreshToken(claims, username);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        log.info("Extract Username for token: {} with type: {}", token, type);

        return extractClaims(type, token, Claims::getSubject);
    }

    private <T> T extractClaims(TokenType type, String token, Function<Claims, T> claimsResolver)  {
        final Claims claims = extraAllClaim(token, type);
        return claimsResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) {
        try {
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied! error: " + e.getMessage());
        }
    }

    private String generateToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryDay))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type) {
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> throw new InvalidDataException("Invalid token type");

        }
    }
}

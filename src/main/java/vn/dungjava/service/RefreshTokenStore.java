package vn.dungjava.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final StringRedisTemplate stringRedisTemplate;

    private static final Duration REFRESH_TOKEN_EXPIRE = Duration.ofDays(30);
    private static final String PREFIX = "rt";

    public void store(String refreshToken, String userName) {
        store(refreshToken, userName, REFRESH_TOKEN_EXPIRE);
    }

    public void store(String refreshToken, String userName, Duration ttl) {
        Objects.requireNonNull(refreshToken, "refreshToken");
        Objects.requireNonNull(userName, "userName");
        Objects.requireNonNull(ttl, "ttl");

        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }

        stringRedisTemplate.opsForValue().set(key(refreshToken), userName, ttl);
    }

    public boolean exists(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty())
            return false;
        return stringRedisTemplate.hasKey(key(refreshToken));
    }

    public void revoke(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty())
            return;
        stringRedisTemplate.delete(key(refreshToken));
    }

    public String getUserName(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty())
            return null;
        return stringRedisTemplate.opsForValue().get(key(refreshToken));
    }


    private String key(String refreshToken) {
        return "rt:" + sha256(refreshToken);
    }

    private String sha256(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot hash refresh token",e);
        }
    }
}

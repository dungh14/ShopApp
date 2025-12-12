package vn.dungjava.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dungjava.common.UserStatus;
import vn.dungjava.model.EmailVerificationToken;
import vn.dungjava.model.User;
import vn.dungjava.repository.EmailVerificationTokenRepository;
import vn.dungjava.repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenService {

    private final EmailVerificationTokenRepository tokenRepo;
    private final UserRepository userRepository;

    // Token sống 15 phút (tuỳ bạn)
    private static final long EXPIRY_MILLIS = 15 * 60 * 1000L;

    @Transactional
    public EmailVerificationToken createOrReplaceToken(User user) {
        // đảm bảo user đã có id
        if (user.getId() == null) {
            user = userRepository.save(user);
        }

        // mỗi user chỉ có 1 token active
        tokenRepo.deleteByUser_Id(user.getId());

        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MILLIS);

        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString().replace("-", "")) // 32 ký tự
                .createdAt(now)
                .expiryDate(expiry)
                .build();

        return tokenRepo.save(token);
    }

    @Transactional
    public void verifyByToken(String tokenValue) {
        EmailVerificationToken token = tokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (token.getExpiryDate().before(new Date())) {
            // token hết hạn -> xoá token
            tokenRepo.deleteById(token.getId());
            throw new IllegalArgumentException("Token expired");
        }

        User user = token.getUser();

        // cập nhật trạng thái user
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // token dùng xong thì xoá
        tokenRepo.deleteById(token.getId());
    }

    @Transactional(readOnly = true)
    public User getUserByToken(String tokenValue) {
        return tokenRepo.findByToken(tokenValue)
                .map(EmailVerificationToken::getUser)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }
}
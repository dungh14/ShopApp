package vn.dungjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dungjava.model.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUser_Id(Long userId);
}

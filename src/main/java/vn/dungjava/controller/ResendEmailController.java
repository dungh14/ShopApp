package vn.dungjava.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.dungjava.model.EmailVerificationToken;
import vn.dungjava.model.User;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.EmailService;
import vn.dungjava.service.EmailVerificationTokenService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResendEmailController {

    private final UserRepository userRepository;
    private final EmailVerificationTokenService tokenService;
    private final EmailService emailService;

    @PostMapping("/resend-verification")
    public void resend(@RequestParam String email) throws IOException {
        User user = userRepository.getByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email not found");
        }

        EmailVerificationToken token = tokenService.createOrReplaceToken(user);
        emailService.emailVerification(user.getEmail(), user.getUsername(), token.getToken());
        log.info("Resend verification done for {}", email);
    }
}

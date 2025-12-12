package vn.dungjava.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.dungjava.service.EmailService;
import vn.dungjava.service.EmailVerificationTokenService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
public class EmailController {

    private final EmailService emailService;
    private final EmailVerificationTokenService tokenService;

    @GetMapping("/send-email")
    public void sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        log.info("Sending email to {}", to);
        emailService.send(to, subject, body);
        log.info("Email sent to {}", to);
    }

    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam("secretCode") String token, HttpServletResponse response) throws IOException {
        log.info("Confirm email token={}", token);
        try {
            tokenService.verifyByToken(token);
            // redirect success page
            response.sendRedirect("https://www.google.com");
        } catch (Exception e) {
            log.error("Confirm email failed: {}", e.getMessage(), e);
            // redirect fail page
            response.sendRedirect("https://www.google.com/search?q=verify+failed");
        }

    }
}

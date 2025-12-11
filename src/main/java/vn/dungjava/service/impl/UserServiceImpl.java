package vn.dungjava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dungjava.common.UserStatus;
import vn.dungjava.common.UserType;
import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserPasswordRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.UserResponse;
import vn.dungjava.exception.InvalidDataException;
import vn.dungjava.exception.ResourceNotFoundException;
import vn.dungjava.model.User;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.UserService;

import java.util.List;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> findAll(String keyword, String sort, int page, int pageSize) {
        return List.of();
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("user not found"));

        return UserResponse.builder()
                .id(id)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(UserCreationRequest req) {
        log.info("Saving User: {}", req);

        User userByEmail = userRepository.getByEmail(req.getEmail());
        if (userByEmail != null) {
            throw new InvalidDataException("Email already exists");
        }

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setType(req.getType());
        user.setStatus(UserStatus.NONE);

        userRepository.save(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Updating User: {}", req);

        User user = userRepository.findById(req.getId()).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());

        userRepository.save(user);
        log.info("Updated User: {}", req);
    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing Password for user: {}", req.getId());

        User user = userRepository.findById(req.getId()).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if(req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        userRepository.save(user);
        log.info("Updated Password for user: {}", req.getId());
    }

    @Override
    public void delete(Long id) {
        log.info("Delete user: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Deleted User: {}", id);
    }
}

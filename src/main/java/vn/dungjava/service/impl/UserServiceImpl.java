package vn.dungjava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.dungjava.common.UserStatus;
import vn.dungjava.common.UserType;
import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserPasswordRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.UserPageResponse;
import vn.dungjava.controller.response.UserResponse;
import vn.dungjava.exception.InvalidDataException;
import vn.dungjava.exception.ResourceNotFoundException;
import vn.dungjava.model.EmailVerificationToken;
import vn.dungjava.model.User;
import vn.dungjava.repository.EmailVerificationTokenRepository;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.EmailService;
import vn.dungjava.service.EmailVerificationTokenService;
import vn.dungjava.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationTokenService tokenService;

    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int pageSize) {

        //Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        if(StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()) {
                String columnName = matcher.group(1);
               if(matcher.group(3).equalsIgnoreCase("asc")) {
                   order = new Sort.Order(Sort.Direction.ASC, columnName);
               } else {
                   order = new Sort.Order(Sort.Direction.DESC, columnName);
               }
            }
        }

        //Xu ly truong hop FE muon trang bat dau voi page = 1
        int pageNo = 0;
        if(page > 0) {
            pageNo = page - 1;
        }

        //Paging
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(order));
        Page<User> entityPage;

        if(StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = userRepository.searchByKeywords(keyword, pageable);
        } else {
            entityPage = userRepository.findAll(pageable);
        }
        return getUserPageResponse(page, pageSize, entityPage);
    }

    private UserPageResponse getUserPageResponse(int page, int pageSize, Page<User> entityPage) {
        List<UserResponse> responseList = entityPage.stream()
                .map(entity -> new UserResponse().builder()
                        .id(entity.getId())
                        .firstName(entity.getFirstName())
                        .lastName(entity.getLastName())
                        .gender(entity.getGender())
                        .dateOfBirth(entity.getDateOfBirth())
                        .username(entity.getUsername())
                        .email(entity.getEmail())
                        .phone(entity.getPhone())
                        .build())
                .toList();

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(page);
        userPageResponse.setPageSize(pageSize);
        userPageResponse.setTotalPages(entityPage.getTotalPages());
        userPageResponse.setTotalElements(entityPage.getTotalElements());
        userPageResponse.setUsers(responseList);
        return userPageResponse;
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
    public long save(UserCreationRequest req){
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

        // tạo token trong DB
        EmailVerificationToken token = tokenService.createOrReplaceToken(user);

        // gửi mail (nếu fail sẽ throw RuntimeException -> rollback cả user + token)
        boolean sent = emailService.emailVerification(user.getEmail(), user.getUsername(), token.getToken());
        if (!sent) log.warn("Email not sent, user created anyway. userId={}", user.getId());

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
        } else {
            throw new InvalidDataException("Passwords do not match");
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

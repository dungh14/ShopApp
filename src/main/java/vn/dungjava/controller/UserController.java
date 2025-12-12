package vn.dungjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserPasswordRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.ApiResponse;
import vn.dungjava.service.EmailVerificationTokenService;
import vn.dungjava.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER_CONTROLLER")
@Validated
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    private final EmailVerificationTokenService tokenService;


    @GetMapping("/list")
    public ApiResponse getListUser(@RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String sort,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        log.info("getListUser");
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get user detail", description = "API get user details")
    @GetMapping("/{userId}")
    public ApiResponse getUserDetails(@PathVariable @Min(value = 1, message = "userId must be equal or greater than 1") Long userId) {
        log.info("getUser");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user list")
                .data(userService.findById(userId))
                .build();
    }

    @Operation(summary = "Create user", description = "API add new user to db")
    @PostMapping("/add")
    public ApiResponse createUser(@RequestBody @Valid UserCreationRequest user) throws IOException {
        log.info("createUser");

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("user created successfully")
                .data(userService.save(user))
                .build();
    }

    @Operation(summary = "Update user", description = "API update user")
    @PutMapping("/upd")
    public ApiResponse updateUser(@RequestBody @Valid UserUpdateRequest request) {
        log.info("updateUser");

        userService.update(request);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("user created successfully")
                .build();
    }

    @Operation(summary = "Change password", description = "API update password for user")
    @PatchMapping("/change-pwd")
    public ApiResponse changePassword(@RequestBody @Valid UserPasswordRequest password) {
        log.info("changePassword");

        userService.changePassword(password);

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Password updated successfully")
                .build();
    }

    @Operation(summary = "Delete user", description = "API inactive user")
    @DeleteMapping("/del/{userId}")
    public ApiResponse deleteUser(@PathVariable @Min(value = 1, message = "userId must be equal or greater than 1") Long userId) {
        log.info("deleteUser");

        userService.delete(userId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("User deleted successfully")
                .build();
    }
}

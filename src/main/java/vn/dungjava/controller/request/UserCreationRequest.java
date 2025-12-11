package vn.dungjava.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import vn.dungjava.common.Gender;
import vn.dungjava.common.UserType;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;
    private Date dateOfBirth;
    private String username;

    @Email(message = "Email invalid")
    private String email;
    private String phone;

    @NotNull(message = "TYpe is required")
    private UserType type;
}

package vn.dungjava.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.dungjava.common.Gender;
import vn.dungjava.common.UserType;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class UserUpdateRequest implements Serializable {

    @NotNull(message = "id must be not null")
    @Min(value = 1, message = "userId must be equal or greater than 1")
    private Long id;

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
}

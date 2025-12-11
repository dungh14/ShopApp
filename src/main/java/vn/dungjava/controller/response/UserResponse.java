package vn.dungjava.controller.response;

import lombok.*;
import vn.dungjava.common.Gender;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    private String username;
    private String email;
    private String phone;
}

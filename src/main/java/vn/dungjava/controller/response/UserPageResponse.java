package vn.dungjava.controller.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageResponse extends UserPageResponseAbstract implements Serializable {

    List<UserResponse> users;
}

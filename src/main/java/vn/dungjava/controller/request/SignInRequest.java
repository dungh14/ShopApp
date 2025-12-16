package vn.dungjava.controller.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {
    private String username;
    private String password;
    private String platform;
    private String deviceToken;
    private String versionApp;
}

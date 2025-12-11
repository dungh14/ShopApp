package vn.dungjava.controller.request;

import java.io.Serializable;

public class SignInRequest implements Serializable {
    private String username;
    private String password;
    private String platform;
    private String deviceToken;
    private String versionApp;
}

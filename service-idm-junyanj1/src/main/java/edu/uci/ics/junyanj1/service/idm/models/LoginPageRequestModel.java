package edu.uci.ics.junyanj1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class LoginPageRequestModel {
    private String email;
    private char[] password;
    public String email_regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public LoginPageRequestModel() {}

    @JsonCreator
    public LoginPageRequestModel(@JsonProperty(value = "email", required = true) String email,
                                 @JsonProperty(value = "password", required = true) char[] password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public char[] getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginPageRequestModel{" +
                "email='" + email + '\'' +
                ", password=" + Arrays.toString(password) +
                '}';
    }
}

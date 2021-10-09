package edu.uci.ics.junyanj1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.Model;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

import java.util.Arrays;

public class LoginUserRequestModel extends RequestModel {
    private String email;
    private char[] password;

    public LoginUserRequestModel() {
    }

    @JsonCreator
    public LoginUserRequestModel(@JsonProperty(value = "email", required = true) String email,
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
        return "LoginUserRequestModel{" +
                "email='" + email + '\'' +
                ", password=" + Arrays.toString(password) +
                '}';
    }
}

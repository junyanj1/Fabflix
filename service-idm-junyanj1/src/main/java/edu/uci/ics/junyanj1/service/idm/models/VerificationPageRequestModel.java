package edu.uci.ics.junyanj1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VerificationPageRequestModel {
    private String email;
    private String sessionID;
    public String email_regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public VerificationPageRequestModel() {}

    @JsonCreator
    public VerificationPageRequestModel(@JsonProperty(value = "email", required = true) String email,
                                        @JsonProperty(value = "sessionID", required = true) String sessionID) {
        this.email = email;
        this.sessionID = sessionID;
    }

    public String getEmail() {
        return email;
    }

    public String getSessionID() {
        return sessionID;
    }

    @Override
    public String toString() {
        return "email: " + email + ", sessionID: " + sessionID;
    }
}

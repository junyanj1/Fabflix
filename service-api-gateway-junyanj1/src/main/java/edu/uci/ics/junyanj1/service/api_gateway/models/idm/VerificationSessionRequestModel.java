package edu.uci.ics.junyanj1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class VerificationSessionRequestModel extends RequestModel {
    private String email;
    private String sessionID;

    @JsonCreator
    public VerificationSessionRequestModel(
            @JsonProperty(value = "email", required = true) String email,
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
        return "VerificationSessionRequestModel{" +
                "email='" + email + '\'' +
                ", sessionID='" + sessionID + '\'' +
                '}';
    }
}

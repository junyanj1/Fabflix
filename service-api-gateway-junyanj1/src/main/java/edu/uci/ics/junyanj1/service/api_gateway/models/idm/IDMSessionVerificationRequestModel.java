package edu.uci.ics.junyanj1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDMSessionVerificationRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "sessionID", required = true)
    private String sessionID;

    @JsonCreator
    public IDMSessionVerificationRequestModel(@JsonProperty(value = "email", required = true) String email,
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
        return "IDMSessionVerificationRequestModel{" +
                "email='" + email + '\'' +
                ", sessionID='" + sessionID + '\'' +
                '}';
    }
}

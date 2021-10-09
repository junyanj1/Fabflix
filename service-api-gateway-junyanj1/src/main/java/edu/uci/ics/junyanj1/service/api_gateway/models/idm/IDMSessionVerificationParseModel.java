package edu.uci.ics.junyanj1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDMSessionVerificationParseModel {
    private int resultCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionID = null;

    public IDMSessionVerificationParseModel() {
    }

    public IDMSessionVerificationParseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                            @JsonProperty(value = "message", required = true) String message,
                                            @JsonProperty(value = "sessionID") String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}

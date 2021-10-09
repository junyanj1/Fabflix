package edu.uci.ics.junyanj1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.Model;
import edu.uci.ics.junyanj1.service.api_gateway.utilities.ResultCodes;

@JsonIgnoreProperties(value = { "dataValid" })
public class VerificationSessionResponseModel extends Model {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionID = null;

    @JsonCreator
    public VerificationSessionResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "sessionID") String sessionID) {
        this.resultCode = resultCode;
        this.message = ResultCodes.setMessage(resultCode);
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

    @Override
    public String toString() {
        return "VerificationSessionResponseModel{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", sessionID='" + sessionID + '\'' +
                '}';
    }
}

package edu.uci.ics.junyanj1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.Model;

@JsonIgnoreProperties(value = { "dataValid" })
public class PrivilegeCheckResponseModel extends Model {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonCreator
    public PrivilegeCheckResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PrivilegeCheckResponseModel{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                '}';
    }
}

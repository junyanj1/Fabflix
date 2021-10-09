package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IDMPrivilegeResponseParseModel {
    private int resultCode;
    private String message;

    public IDMPrivilegeResponseParseModel() {
    }

    @JsonCreator
    public IDMPrivilegeResponseParseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
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
        return "IDMPrivilegeResponseParseModel{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                '}';
    }
}

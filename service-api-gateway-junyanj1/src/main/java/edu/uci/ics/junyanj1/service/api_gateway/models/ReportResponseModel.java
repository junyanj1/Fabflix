package edu.uci.ics.junyanj1.service.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.utilities.ResultCodes;

public class ReportResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    private ReportResponseModel() {
    }

    private ReportResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static ReportResponseModel reportResponseModelFactory(int rc) {
        return new ReportResponseModel(rc,ResultCodes.setMessage(rc));
    }
}

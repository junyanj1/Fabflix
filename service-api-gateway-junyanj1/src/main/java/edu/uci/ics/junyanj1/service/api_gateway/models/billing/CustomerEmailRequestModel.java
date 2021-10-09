package edu.uci.ics.junyanj1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class CustomerEmailRequestModel extends RequestModel {
    private String email;

    @JsonCreator
    public CustomerEmailRequestModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CustomerEmailRequestModel{" +
                "email='" + email + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }
}

package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerEmailRequestModel {
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

package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequestModel {
    private String email;

    @JsonCreator
    public OrderRequestModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "OrderRequestModel{" +
                "email='" + email + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }
}

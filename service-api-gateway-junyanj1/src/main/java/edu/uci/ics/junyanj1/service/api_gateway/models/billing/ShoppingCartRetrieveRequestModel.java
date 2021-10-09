package edu.uci.ics.junyanj1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class ShoppingCartRetrieveRequestModel extends RequestModel {
    @JsonProperty(value = "email",required = true)
    private String email;

    @JsonCreator
    public ShoppingCartRetrieveRequestModel(@JsonProperty(value = "email",required = true) String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ShoppingCartRetrieveRequestModel{" +
                "email='" + email + '\'' +
                '}';
    }

    @JsonProperty(value = "email",required = true)
    public String getEmail() {
        return email;
    }
}

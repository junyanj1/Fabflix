package edu.uci.ics.junyanj1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class CreditCardManipulationRequestModel extends RequestModel {
    private String id;

    public CreditCardManipulationRequestModel(@JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CreditCardManipulationRequestModel{" +
                "id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }
}

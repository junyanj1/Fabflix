package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardManipulationRequestModel {
    private String id;

    @JsonCreator
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

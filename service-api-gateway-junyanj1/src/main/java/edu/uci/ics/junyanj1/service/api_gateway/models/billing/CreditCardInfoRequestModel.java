package edu.uci.ics.junyanj1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

import java.sql.Date;

public class CreditCardInfoRequestModel extends RequestModel {
    private String id;
    private String firstName;
    private String lastName;
    private Date expiration;
    final long hours12 = 12L * 60L * 60L * 1000L;

    @JsonCreator
    public CreditCardInfoRequestModel(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "firstName", required = true) String firstName,
            @JsonProperty(value = "lastName", required = true) String lastName,
            @JsonProperty(value = "expiration", required = true) Date expiration
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiration = new Date(expiration.getTime() + hours12);
    }

    @Override
    public String toString() {
        return "CreditCardInfoRequestModel{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", expiration=" + expiration +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getExpiration() {
        return expiration;
    }
}

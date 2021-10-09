package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AmountModel {
    @JsonProperty(value = "total", required = true)
    private String total;
    @JsonProperty(value = "currency", required = true)
    private String currency;

    @JsonCreator
    public AmountModel(@JsonProperty(value = "total", required = true) String total,
                       @JsonProperty(value = "currency", required = true) String currency) {
        this.total = total;
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "AmountModel{" +
                "total='" + total + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}

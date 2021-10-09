package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

public class OrderModel extends ShoppingCartModel {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date saleDate;

    @JsonCreator
    public OrderModel(@JsonProperty(value = "email", required = true) String email,
                      @JsonProperty(value = "movieId", required = true) String movieId,
                      @JsonProperty(value = "quantity", required = true) int quantity,
                      @JsonProperty(value = "saleDate", required = true) Date saleDate) {
        super(email, movieId, quantity);
        this.saleDate = saleDate;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
}

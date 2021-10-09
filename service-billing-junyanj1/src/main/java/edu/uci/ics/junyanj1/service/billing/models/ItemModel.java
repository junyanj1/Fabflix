package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.text.DecimalFormat;

public class ItemModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "quantity", required = true)
    private int quantity;
    @JsonProperty(value = "unit_price", required = true)
    private String unit_price;
    @JsonProperty(value = "discount", required = true)
    private String discount;
    @JsonProperty(value = "saleDate", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String saleDate;

    @JsonCreator
    public ItemModel(@JsonProperty(value = "email", required = true) String email,
                     @JsonProperty(value = "movieId", required = true) String movieId,
                     @JsonProperty(value = "quantity", required = true) int quantity,
                     @JsonProperty(value = "unit_price", required = true) float unit_price,
                     @JsonProperty(value = "discount", required = true) float discount,
                     @JsonProperty(value = "saleDate", required = true) Date saleDate) {
        DecimalFormat df = new DecimalFormat("0.00");

        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.unit_price = df.format(unit_price);
        this.discount = df.format(discount);
        this.saleDate = saleDate.toString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    @Override
    public String toString() {
        return "ItemModel{" +
                "email='" + email + '\'' +
                ", movieId='" + movieId + '\'' +
                ", quantity=" + quantity +
                ", unit_price=" + unit_price +
                ", discount=" + discount +
                ", saleDate='" + saleDate + '\'' +
                '}';
    }
}

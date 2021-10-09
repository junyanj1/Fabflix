package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionModel {
    @JsonProperty(value = "transactionId", required = true)
    private String transactionId;
    @JsonProperty(value = "state", required = true)
    private String state;
    @JsonProperty(value = "amount", required = true)
    private AmountModel amount;
    @JsonProperty(value = "transaction_fee", required = true)
    private TransactionFeeModel transaction_fee;
    @JsonProperty(value = "create_time", required = true)
    private String create_time;
    @JsonProperty(value = "update_time", required = true)
    private String update_time;
    @JsonProperty(value = "items")
    private ArrayList<ItemModel> items;

    public TransactionModel(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionModel(String transactionId, String state, AmountModel amount, TransactionFeeModel transaction_fee, String create_time, String update_time) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    public TransactionModel(String transactionId, String state, AmountModel amount, TransactionFeeModel transaction_fee, String create_time, String update_time, ArrayList<ItemModel> items) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AmountModel getAmount() {
        return amount;
    }

    public void setAmount(AmountModel amount) {
        this.amount = amount;
    }

    public TransactionFeeModel getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(TransactionFeeModel transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public ArrayList<ItemModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemModel> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "transactionId='" + transactionId + '\'' +
                ", state='" + state + '\'' +
                ", amount=" + amount +
                ", transaction_fee=" + transaction_fee +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", items=" + items +
                '}';
    }
}

package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;

import java.util.ArrayList;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "transactions")
    private ArrayList<TransactionModel> transactions;

    private OrderRetrieveResponseModel() {
    }

    private OrderRetrieveResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    private OrderRetrieveResponseModel(int resultCode, String message, ArrayList<TransactionModel> transactions) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = transactions;
    }

    public static OrderRetrieveResponseModel orderRetrieveResponseModelFactory(int rc, ArrayList<TransactionModel> transactions) {
        ServiceLogger.LOGGER.info("Starting to construct order retrieve response model.");
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            return new OrderRetrieveResponseModel(-3,"JSON Parse Exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            return new OrderRetrieveResponseModel(-2,"JSON Mapping Exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal Server Error.");
            return new OrderRetrieveResponseModel(-1,"Internal Server Error.");
        } else if (rc == 332) {
            ServiceLogger.LOGGER.info("Case 332: Customer does not exist.");
            return new OrderRetrieveResponseModel(332,"Customer does not exist.");
        } else if (rc == 3410) {
            ServiceLogger.LOGGER.info("Case 3410: Orders retrieved successfully.");
            return new OrderRetrieveResponseModel(3410,"Orders retrieved successfully.",transactions);
        }
        return new OrderRetrieveResponseModel();
    }
}

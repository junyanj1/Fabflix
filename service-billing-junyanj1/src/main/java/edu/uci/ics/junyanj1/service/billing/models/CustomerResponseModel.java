package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponseModel {
    @JsonProperty(value = "resultCode",required = true)
    private int resultCode;
    @JsonProperty(value = "message",required = true)
    private String message;
    @JsonProperty(value = "customer", required = true)
    private CustomerModel customer;

    private CustomerResponseModel() {
    }

    private CustomerResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    private CustomerResponseModel(int resultCode, String message, CustomerModel customer) {
        this.resultCode = resultCode;
        this.message = message;
        this.customer = customer;
    }

    public static CustomerResponseModel customerResponseModelFactory(int rc, CustomerModel cm) {
        ServiceLogger.LOGGER.info("Starting to construct customer response model.");
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            return new CustomerResponseModel(-3,"JSON Parse Exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            return new CustomerResponseModel(-2,"JSON Mapping Exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal Server Error.");
            return new CustomerResponseModel(-1,"Internal Server Error.");
        } else if (rc == 321) {
            ServiceLogger.LOGGER.info("Case 321: Credit card ID has invalid length.");
            return new CustomerResponseModel(321,"Credit card ID has invalid length.");
        } else if (rc == 322) {
            ServiceLogger.LOGGER.info("Case 322: Credit card ID has invalid value.");
            return new CustomerResponseModel(322,"Credit card ID has invalid value.");
        } else if (rc == 331) {
            ServiceLogger.LOGGER.info("Case 331: Credit card ID not found.");
            return new CustomerResponseModel(331,"Credit card ID not found.");
        } else if (rc == 332) {
            ServiceLogger.LOGGER.info("Case 332: Customer does not exist.");
            return new CustomerResponseModel(332,"Customer does not exist.");
        } else if (rc == 333) {
            ServiceLogger.LOGGER.info("Case 333: Duplicate insertion.");
            return new CustomerResponseModel(333,"Duplicate insertion.");
        } else if (rc == 3300) {
            ServiceLogger.LOGGER.info("Case 3300: Customer inserted successfully.");
            return new CustomerResponseModel(3300,"Customer inserted successfully.");
        } else if (rc == 3310) {
            ServiceLogger.LOGGER.info("Case 3310: Customer updated successfully.");
            return new CustomerResponseModel(3310,"Customer updated successfully.");
        } else if (rc == 3320) {
            ServiceLogger.LOGGER.info("Case 3320: Customer retrieved successfully.");
            return new CustomerResponseModel(3320,"Customer retrieved successfully.",cm);
        }
        return new CustomerResponseModel();
    }
}

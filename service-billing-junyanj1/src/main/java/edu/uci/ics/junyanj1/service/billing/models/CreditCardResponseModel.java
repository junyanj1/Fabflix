package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "creditcard")
    private CreditCardModel creditcard;

    private CreditCardResponseModel() {
    }

    private CreditCardResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    private CreditCardResponseModel(int resultCode, String message, CreditCardModel creditcard) {
        this.resultCode = resultCode;
        this.message = message;
        this.creditcard = creditcard;
    }

    public static CreditCardResponseModel creditCardResponseModelFactory (int rc, CreditCardModel ccm) {
        ServiceLogger.LOGGER.info("Starting to construct credit card response model.");
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            return new CreditCardResponseModel(-3,"JSON Parse Exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            return new CreditCardResponseModel(-2,"JSON Mapping Exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal Server Error.");
            return new CreditCardResponseModel(-1,"Internal Server Error.");
        } else if (rc == 321) {
            ServiceLogger.LOGGER.info("Case 321: Credit card ID has invalid length.");
            return new CreditCardResponseModel(321,"Credit card ID has invalid length.");
        } else if (rc == 322) {
            ServiceLogger.LOGGER.info("Case 322: Credit card ID has invalid value.");
            return new CreditCardResponseModel(322,"Credit card ID has invalid value.");
        } else if (rc == 323) {
            ServiceLogger.LOGGER.info("Case 323: expiration has invalid value.");
            return new CreditCardResponseModel(323,"expiration has invalid value.");
        } else if (rc == 324) {
            ServiceLogger.LOGGER.info("Case 324: Credit card does not exist.");
            return new CreditCardResponseModel(324,"Credit card does not exist.");
        } else if (rc == 325) {
            ServiceLogger.LOGGER.info("Case 325: Duplicate insertion.");
            return new CreditCardResponseModel(325,"Duplicate insertion.");
        } else if (rc == 3200) {
            ServiceLogger.LOGGER.info("Case 3200: Credit card inserted successfully.");
            return new CreditCardResponseModel(3200,"Credit card inserted successfully.");
        } else if (rc == 3210) {
            ServiceLogger.LOGGER.info("Case 3210: Credit card updated successfully.");
            return new CreditCardResponseModel(3210,"Credit card updated successfully.");
        } else if (rc == 3220) {
            ServiceLogger.LOGGER.info("Case 3220: Credit card deleted successfully.");
            return new CreditCardResponseModel(3220,"Credit card deleted successfully.");
        } else if (rc == 3230) {
            ServiceLogger.LOGGER.info("Case 3230: Credit card retrieved successfully.");
            return new CreditCardResponseModel(3230, "Credit card retrieved successfully.",ccm);
        }
        return new CreditCardResponseModel();
    }
}

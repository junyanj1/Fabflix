package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;

import java.util.ArrayList;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "items")
    private ArrayList<ShoppingCartModel> items;

    private ShoppingCartResponseModel() {
    }

    private ShoppingCartResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    private ShoppingCartResponseModel(int resultCode, String message, ArrayList<ShoppingCartModel> items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
    }

    public static ShoppingCartResponseModel shoppingCartResponseModelFactory(int rc, ArrayList<ShoppingCartModel> scm) {
        ServiceLogger.LOGGER.info("Starting to construct shopping cart response model.");
        if (rc == -11) {
            ServiceLogger.LOGGER.info("Case -11: Email address has invalid format.");
            return new ShoppingCartResponseModel(-11, "Email address has invalid format.");
        } else if (rc == -10) {
            ServiceLogger.LOGGER.info("Case -10: Email address has invalid length.");
            return new ShoppingCartResponseModel(-10, "Email address has invalid length.");
        } else if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            return new ShoppingCartResponseModel(-3,"JSON Parse Exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            return new ShoppingCartResponseModel(-2,"JSON Mapping Exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal Server Error.");
            return new ShoppingCartResponseModel(-1,"Internal Server Error.");
        } else if (rc == 33) {
            ServiceLogger.LOGGER.info("Case 33: Quantity has invalid value.");
            return new ShoppingCartResponseModel(33,"Quantity has invalid value.");
        } else if (rc == 311) {
            ServiceLogger.LOGGER.info("Case 311: Duplicate insertion.");
            return new ShoppingCartResponseModel(311,"Duplicate insertion.");
        } else if (rc == 3100) {
            ServiceLogger.LOGGER.info("Case 3100: Shopping cart item inserted successfully.");
            return new ShoppingCartResponseModel(3100,"Shopping cart item inserted successfully.");
        } else if (rc == 312) {
            ServiceLogger.LOGGER.info("Case 312: Shopping item does not exist.");
            return new ShoppingCartResponseModel(312,"Shopping item does not exist.");
        } else if (rc == 3110) {
            ServiceLogger.LOGGER.info("Case 3110: Shopping cart item updated successfully.");
            return new ShoppingCartResponseModel(3110,"Shopping cart item updated successfully.");
        } else if (rc == 3120) {
            ServiceLogger.LOGGER.info("Case 3120: Shopping cart item deleted successfully.");
            return new ShoppingCartResponseModel(3120, "Shopping cart item deleted successfully.");
        } else if (rc == 3130) {
            ServiceLogger.LOGGER.info("Case 3130: Shopping cart retrieved successfully.");
            return new ShoppingCartResponseModel(3130,"Shopping cart retrieved successfully.",scm);
        } else if (rc == 3140) {
            ServiceLogger.LOGGER.info("Case 3140: Shopping cart cleared successfully.");
            return new ShoppingCartResponseModel(3140,"Shopping cart cleared successfully.");
        }
        return new ShoppingCartResponseModel();
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<ShoppingCartModel> getItems() {
        return items;
    }
}

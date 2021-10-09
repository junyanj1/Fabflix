package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
//    @JsonProperty(value = "items", required = true)
//    private ArrayList<OrderModel> items;
    @JsonProperty(value = "redirectURL")
    private String redirectURL;
    @JsonProperty(value = "token")
    private String token;

    private OrderResponseModel() {
    }

    private OrderResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

//    private OrderResponseModel(int resultCode, String message, ArrayList<OrderModel> items) {
//        this.resultCode = resultCode;
//        this.message = message;
//        this.items = items;
//    }

    public static Map<String,String> mapQuery(String query) {
        String[] qs = query.split("&");
        Map<String,String> m = new HashMap<String,String>();
        for (String i:qs) {
            String name = i.split("=")[0];
            String value = i.split("=")[1];
            m.put(name,value);
        }
        return m;
    }

    private OrderResponseModel(int resultCode, String message, String redirectURL) {
        this.resultCode = resultCode;
        this.message = message;
        this.redirectURL = redirectURL;
        URL url = null;
        try {
            url = new URL(this.redirectURL);
            String query = url.getQuery();
            Map<String,String> map = mapQuery(query);
            this.token = map.get("token");

        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Failed to parse the payment url: "+redirectURL);
            e.printStackTrace();
        }
    }

    public static OrderResponseModel orderResponseModelFactory(int rc, String redirectURL) {
        ServiceLogger.LOGGER.info("Starting to construct order response model.");
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            return new OrderResponseModel(-3,"JSON Parse Exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            return new OrderResponseModel(-2,"JSON Mapping Exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal Server Error.");
            return new OrderResponseModel(-1,"Internal Server Error.");
        } else if (rc == 332) {
            ServiceLogger.LOGGER.info("Case 332: Customer does not exist.");
            return new OrderResponseModel(332,"Customer does not exist.");
        } else if (rc == 341) {
            ServiceLogger.LOGGER.info("Case 341: Shopping cart for this customer not found.");
            return new OrderResponseModel(341,"Shopping cart for this customer not found.");
        } else if (rc == 3400) {
            ServiceLogger.LOGGER.info("Case 3400: Order placed successfully.");
            ServiceLogger.LOGGER.info("redirectURL: " + redirectURL);
            return new OrderResponseModel(3400, "Order placed successfully.",redirectURL);
        } else if (rc == 3410) {
            ServiceLogger.LOGGER.info("Case 3410: Orders retrieved successfully.");
            return new OrderResponseModel(3410,"Orders retrieved successfully.");
        } else if (rc == 342) {
            ServiceLogger.LOGGER.info("Case 342: Create payment failed.");
            return new OrderResponseModel(342,"Create payment failed.");
        } else if (rc == 3421) {
            ServiceLogger.LOGGER.info("Case 3421: Token not found.");
            return new OrderResponseModel(3421,"Token not found.");
        } else if (rc == 3422) {
            ServiceLogger.LOGGER.info("Case 3422: Payment can not be completed.");
            return new OrderResponseModel(3422,"Payment can not be completed.");
        } else if (rc == 3420) {
            ServiceLogger.LOGGER.info("Case 3420: Payment is completed successfully.");
            return new OrderResponseModel(3420,"Payment is completed successfully.");
        }
        return new OrderResponseModel();
    }

    public String getToken() {
        return token;
    }
}

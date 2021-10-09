package edu.uci.ics.junyanj1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.Model;
import edu.uci.ics.junyanj1.service.api_gateway.utilities.ResultCodes;

@JsonIgnoreProperties(value = { "dataValid" })
public class ShoppingCartInsertResponseModel extends Model {
    private int resultCode;
    private String message;

    public ShoppingCartInsertResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode) {
        this.resultCode = resultCode;
        this.message = ResultCodes.setMessage(resultCode);
    }

    @Override
    public String toString() {
        return "ShoppingCartInsertResponseModel{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                '}';
    }

    @JsonProperty(value = "resultCode", required = true)
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message", required = true)
    public String getMessage() {
        return message;
    }
}

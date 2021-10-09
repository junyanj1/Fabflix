package edu.uci.ics.junyanj1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class PrivilegeCheckRequestModel extends RequestModel {
    private String email;
    private int plevel;

    @JsonCreator
    public PrivilegeCheckRequestModel(
            @JsonProperty(value = "email",required = true) String email,
            @JsonProperty(value = "plevel",required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    public String getEmail() {
        return email;
    }

    public int getPlevel() {
        return plevel;
    }

    @Override
    public String toString() {
        return "PrivilegeCheckRequestModel{" +
                "email='" + email + '\'' +
                ", plevel=" + plevel +
                '}';
    }
}

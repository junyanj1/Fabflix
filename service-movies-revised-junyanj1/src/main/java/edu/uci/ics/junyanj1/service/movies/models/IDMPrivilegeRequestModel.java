package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDMPrivilegeRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "plevel", required = true)
    private int privilege = 4;

    public IDMPrivilegeRequestModel() {}

    @JsonCreator
    public IDMPrivilegeRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "plevel", required = true) int privilege) {
        this.email = email;
        this.privilege = privilege;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("plevel")
    public int getPrivilege() {
        return privilege;
    }

    @Override
    public String toString() {
        return "IDMPrivilegeRequestModel{" +
                "email='" + email + '\'' +
                ", privilege=" + privilege +
                '}';
    }
}

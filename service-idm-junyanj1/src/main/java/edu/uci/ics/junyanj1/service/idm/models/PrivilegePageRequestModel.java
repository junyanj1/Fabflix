package edu.uci.ics.junyanj1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivilegePageRequestModel {
    private String email;
    private int plevel;
    public String email_regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public PrivilegePageRequestModel() {}

    @JsonCreator
    public PrivilegePageRequestModel(@JsonProperty(value = "email", required = true) String email,
                                     @JsonProperty(value = "plevel", required = true) int plevel) {
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
        return "PrivilegePageRequestModel{" +
                "email='" + email + '\'' +
                ", plevel=" + plevel +
                '}';
    }
}

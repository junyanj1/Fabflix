package edu.uci.ics.junyanj1.service.idm.core;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"valid"})
public interface Validate {
    boolean isValid();
}

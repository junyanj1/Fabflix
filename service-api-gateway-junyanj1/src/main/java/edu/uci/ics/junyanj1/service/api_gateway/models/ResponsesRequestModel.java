package edu.uci.ics.junyanj1.service.api_gateway.models;

public class ResponsesRequestModel {
    private String transactionid;
    private String email;
    private String sessionid;
    private String response;
    private int httpstatus;

    public ResponsesRequestModel(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getHttpstatus() {
        return httpstatus;
    }

    public void setHttpstatus(int httpstatus) {
        this.httpstatus = httpstatus;
    }

    @Override
    public String toString() {
        return "ResponsesRequestModel{" +
                "transactionid='" + transactionid + '\'' +
                ", email='" + email + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", response='" + response + '\'' +
                ", httpstatus=" + httpstatus +
                '}';
    }
}

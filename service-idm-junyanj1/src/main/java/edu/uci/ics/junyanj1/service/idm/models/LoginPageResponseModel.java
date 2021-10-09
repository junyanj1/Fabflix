package edu.uci.ics.junyanj1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;

public class LoginPageResponseModel {
    private int resultCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionID = null;

    public LoginPageResponseModel() {}

    private LoginPageResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    private LoginPageResponseModel(int resultCode, String message, String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public static LoginPageResponseModel LoginPageResponseModelFactory(int rc, String id) {
        ServiceLogger.LOGGER.info("Creating message...");
        LoginPageResponseModel lprm;

        if (rc == -12) {
            ServiceLogger.LOGGER.info("Case -12: Password has invalid length.");
            lprm = new LoginPageResponseModel(rc, "Password has invalid length.");
        }
        else if (rc == -11) {
            ServiceLogger.LOGGER.info("Case -11: Email address has invalid format.");
            lprm = new LoginPageResponseModel(rc, "Email address has invalid format.");
        }
        else if (rc == -10) {
            ServiceLogger.LOGGER.info("Case -10: Email address has invalid length.");
            lprm = new LoginPageResponseModel(rc, "Email address has invalid length.");
        }
        else if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            lprm = new LoginPageResponseModel(rc, "JSON Parse Exception.");
        }
        else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            lprm = new LoginPageResponseModel(rc, "JSON Mapping Exception.");
        }
        else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            lprm = new LoginPageResponseModel();
        }
        else if (rc == 11) {
            ServiceLogger.LOGGER.info("Case 11: Passwords do not match.");
            lprm = new LoginPageResponseModel(rc, "Passwords do not match.");
        }
        else if (rc == 14) {
            ServiceLogger.LOGGER.info("Case 14: User not found.");
            lprm = new LoginPageResponseModel(rc, "User not found.");
        }
        else if (rc == 120) {
            ServiceLogger.LOGGER.info("Case 120: User logged in successfully.");
            lprm = new LoginPageResponseModel(rc, "User logged in successfully.", id);
        }
        else {
            ServiceLogger.LOGGER.warning("Unknown resultCode.");
            lprm = new LoginPageResponseModel();
        }
        ServiceLogger.LOGGER.info("Finished building model. ");
        return lprm;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionID() {
        return sessionID;
    }
}

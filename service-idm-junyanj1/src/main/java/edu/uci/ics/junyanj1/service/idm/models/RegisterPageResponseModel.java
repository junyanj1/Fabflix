package edu.uci.ics.junyanj1.service.idm.models;

import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;

public class RegisterPageResponseModel {
    private int resultCode;
    private String message;

    public RegisterPageResponseModel() {
    }

    private RegisterPageResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    // Factory method
    public static RegisterPageResponseModel registerPageResponseModelFactory(int rc) {
        ServiceLogger.LOGGER.info("Creating model...");
        RegisterPageResponseModel rprm;

        if(rc == -12) {
            ServiceLogger.LOGGER.info("Case -12: Password has invalid length (cannot be empty/null).");
            rprm = new RegisterPageResponseModel(-12, "Password has invalid length.");
        }
        else if (rc == -11) {
            ServiceLogger.LOGGER.info("Case -11: Email address has invalid format.");
            rprm = new RegisterPageResponseModel(-11, "Email address has invalid format.");
        }
        else if (rc == -10) {
            ServiceLogger.LOGGER.info("Case -10: Email address has invalid length.");
            rprm = new RegisterPageResponseModel(-10, "Email address has invalid length.");
        }
        else if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            rprm = new RegisterPageResponseModel(-3, "JSON Parse Exception.");
        }
        else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            rprm = new RegisterPageResponseModel(-2, "JSON Mapping Exception.");
        }
        else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal Server Error.");
            rprm = new RegisterPageResponseModel();
        }
        else if (rc == 12) {
            ServiceLogger.LOGGER.info("Case 12: Password does not meet length requirements.");
            rprm = new RegisterPageResponseModel(12, "Password does not meet length requirements.");
        }
        else if (rc == 13) {
            ServiceLogger.LOGGER.info("Case 13: Password does not meet character requirements.");
            rprm = new RegisterPageResponseModel(13, "Password does not meet character requirements.");
        }
        else if (rc == 16) {
            ServiceLogger.LOGGER.info("Case 16: Email already in use.");
            rprm = new RegisterPageResponseModel(16, "Email already in use.");
        }
        else if (rc == 110) {
            ServiceLogger.LOGGER.info("Case 110: User registered successfully.");
            rprm = new RegisterPageResponseModel(110, "User registered successfully.");
        }
        else {
            ServiceLogger.LOGGER.info("Other case. Return an empty response");
            rprm = new RegisterPageResponseModel();
        }
        ServiceLogger.LOGGER.info("Finished building model. ");
        return rprm;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }
}

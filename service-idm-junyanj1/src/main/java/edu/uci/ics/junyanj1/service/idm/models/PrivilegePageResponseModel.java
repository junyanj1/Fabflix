package edu.uci.ics.junyanj1.service.idm.models;

import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;

public class PrivilegePageResponseModel {
    private int resultCode;
    private String message;

    public PrivilegePageResponseModel() {}

    private PrivilegePageResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static PrivilegePageResponseModel PrivilegePageResponseModelFactory(int rc) {
        ServiceLogger.LOGGER.info("Creating message...");
        PrivilegePageResponseModel pprm;

        if (rc == -14) {
            ServiceLogger.LOGGER.info("Case -14: Privilege level out of valid range.");
            pprm = new PrivilegePageResponseModel(rc, "Privilege level out of valid range.");
        }
        else if (rc == -11) {
            ServiceLogger.LOGGER.info("Case -11: Email address has invalid format.");
            pprm = new PrivilegePageResponseModel(rc, "Email address has invalid format.");
        }
        else if (rc == -10) {
            ServiceLogger.LOGGER.info("Case -10: Email address has invalid length.");
            pprm = new PrivilegePageResponseModel(rc, "Email address has invalid length.");
        }
        else if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            pprm = new PrivilegePageResponseModel(rc, "JSON Parse Exception.");
        }
        else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            pprm = new PrivilegePageResponseModel(rc, "JSON Mapping Exception.");
        }
        else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal Server Error.");
            pprm = new PrivilegePageResponseModel(rc, "Internal Server Error.");
        }
        else if (rc == 14) {
            ServiceLogger.LOGGER.info("Case 14: User not found.");
            pprm = new PrivilegePageResponseModel(rc, "User not found.");
        }
        else if (rc == 140) {
            ServiceLogger.LOGGER.info("Case 140: User has sufficient privilege level.");
            pprm = new PrivilegePageResponseModel(rc, "User has sufficient privilege level.");
        }
        else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141:  User has insufficient privilege level.");
            pprm = new PrivilegePageResponseModel(rc, "User has insufficient privilege level.");
        }
        else {
            ServiceLogger.LOGGER.info("Unknown resultCode.");
            pprm = new PrivilegePageResponseModel();
        }
        ServiceLogger.LOGGER.info("Finished building model. ");
        return pprm;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }
}

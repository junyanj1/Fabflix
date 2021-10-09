package edu.uci.ics.junyanj1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;

public class VerificationPageResponseModel {
    private int resultCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionID = null;

    public VerificationPageResponseModel() {}

    private VerificationPageResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    private VerificationPageResponseModel(int resultCode, String message, String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public static VerificationPageResponseModel verificationPageResponseModelFactory(int rc, String id) {
        ServiceLogger.LOGGER.info("Creating service...");
        VerificationPageResponseModel vprm;

        if (rc == -13) {
            ServiceLogger.LOGGER.info("Case -13: Token has invalid length.");
            vprm = new VerificationPageResponseModel(rc, "Token has invalid length.");
        }
        else if (rc == -11) {
            ServiceLogger.LOGGER.info("Case -11: Email address has invalid format.");
            vprm = new VerificationPageResponseModel(rc, "Email address has invalid format.");
        }
        else if (rc == -10) {
            ServiceLogger.LOGGER.info("Case -10: Email address has invalid length.");
            vprm = new VerificationPageResponseModel(rc, "Email address has invalid length.");
        }
        else if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
            vprm = new VerificationPageResponseModel(rc, "JSON Parse Exception.");
        }
        else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
            vprm = new VerificationPageResponseModel(rc, "JSON Mapping Exception.");
        }
        else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            vprm = new VerificationPageResponseModel(rc, "Internal server error.");
        }
        else if (rc == 14) {
            ServiceLogger.LOGGER.info("Case 14: User not found.");
            vprm = new VerificationPageResponseModel(rc, "User not found.");
        }
        else if (rc == 130) {
            ServiceLogger.LOGGER.info("Case 130: Session is active.");
            vprm = new VerificationPageResponseModel(rc, "Session is active.", id);
        }
        else if (rc == 131) {
            ServiceLogger.LOGGER.info("Case 131:  Session is expired.");
            vprm = new VerificationPageResponseModel(rc, "Session is expired.");
        }
        else if (rc == 132) {
            ServiceLogger.LOGGER.info("Case 132: Session is closed.");
            vprm = new VerificationPageResponseModel(rc, "Session is closed.");
        }
        else if (rc == 133) {
            ServiceLogger.LOGGER.info("Case 133: Session is revoked.");
            vprm = new VerificationPageResponseModel(rc, "Session is revoked.");
        }
        else if (rc == 134) {
            ServiceLogger.LOGGER.info("Case 134: Session not found.");
            vprm = new VerificationPageResponseModel(rc, "Session not found.");
        }
        else {
            ServiceLogger.LOGGER.warning("Unknown resultCode.");
            vprm = new VerificationPageResponseModel();
        }
        ServiceLogger.LOGGER.info("Finished building model.");
        return vprm;
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

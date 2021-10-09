package edu.uci.ics.junyanj1.service.idm.core;

import com.mysql.cj.protocol.Resultset;
import edu.uci.ics.junyanj1.service.idm.IDMService;
import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.idm.models.LoginPageRequestModel;
import edu.uci.ics.junyanj1.service.idm.models.PrivilegePageRequestModel;
import edu.uci.ics.junyanj1.service.idm.models.RegisterPageRequestModel;
import edu.uci.ics.junyanj1.service.idm.models.VerificationPageRequestModel;
import edu.uci.ics.junyanj1.service.idm.security.Crypto;
import edu.uci.ics.junyanj1.service.idm.security.Session;
import edu.uci.ics.junyanj1.service.idm.security.Token;
import org.apache.commons.codec.binary.Hex;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserRecords {
    public static boolean registerUserToDb(RegisterPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Inserting registering user into database...");
        try {
            String query =
                    "INSERT INTO users(email,status,plevel,salt,pword) VALUES(?,1,5,?,?);";
            // Store the hashed + encoded password, and the encoded salt in the database
            byte[] salt = Crypto.genSalt();
            byte[] pword = Crypto.hashPassword(requestModel.getPassword(),salt,Crypto.ITERATIONS,Crypto.KEY_LENGTH);
            String password = Hex.encodeHexString(pword);
            String encodedSalt = Hex.encodeHexString(salt);
            requestModel.resetPassword();

            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, encodedSalt);
            ps.setString(3, password);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to register user " + requestModel.getEmail());
            e.printStackTrace();
        }
        return false;
    }

    public static int isRegisterEmailAvailable(RegisterPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Checking if the registering email is already in database...");
        try{
            String query =
                    "SELECT id FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if(rs.next()) {
                ServiceLogger.LOGGER.info("Database has the register email.");
                return 0;
            }
            else {
                ServiceLogger.LOGGER.info("Database does not have the register email.");
                return 1;
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to check if the email is in database.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int isUserInDb(LoginPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Check if the login user is registered in database...");
        try {
            String query =
                    "SELECT id FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if (rs.next()) {
                ServiceLogger.LOGGER.info("User is in database.");
                return 1;
            } else {
                ServiceLogger.LOGGER.info("User not in database.");
                return 0;
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to check if the login user is in database.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int isUserInDb(VerificationPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Check if the verifying username is registered in database...");
        try {
            String query =
                    "SELECT id FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if (rs.next()) {
                ServiceLogger.LOGGER.info("User is in database.");
                return 1;
            } else {
                ServiceLogger.LOGGER.info("User not in database.");
                return 0;
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to check if the verifying user is in database.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int isUserInDb(PrivilegePageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Check if the verifying username is registered in database...");
        try {
            String query =
                    "SELECT id FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if (rs.next()) {
                ServiceLogger.LOGGER.info("User is in database.");
                return 1;
            }
            else {
                ServiceLogger.LOGGER.info("User not in database.");
                return 0;
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to check if the verifying user is in database.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int doesPasswordMatch(LoginPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Verifying the login password with database record...");
        try {
            String query =
                    "SELECT salt, pword FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if (rs.next()) {
               String salt = rs.getString("salt");
               String pword = rs.getString("pword");
               byte[] decoded_salt = Token.convert(salt);
               String salted_login_pw = Hex.encodeHexString(Crypto.hashPassword(requestModel.getPassword(),decoded_salt,Crypto.ITERATIONS,Crypto.KEY_LENGTH));
               if (salted_login_pw.equals(pword)) {
                   ServiceLogger.LOGGER.info("Password matches.");
                   return 1;
               }
               else {
                   ServiceLogger.LOGGER.info("Passwords don't match.");
                   return 0;
               }
            }
            else {
                ServiceLogger.LOGGER.warning("Unable to acquire salt and password.");
                return -1;
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to verify with database.");
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean revokeExistingSession(LoginPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Search for existing session for the login user and revoke them...");
        try {
            String query =
                    "UPDATE sessions SET status = 4 WHERE email = ? AND status = 1;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded.");
            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to revoke existing sessions for the login user.");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean revokeExistingSession(VerificationPageRequestModel requestModel, int status) {
        ServiceLogger.LOGGER.info("Search for the targeted session and revoke it...");
        try {
            String query =
                    "UPDATE sessions SET status = ? WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1, status);
            ps.setString(2,requestModel.getSessionID());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded.");
            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to revoke the given existing sessions.");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean lastUsedUpdate(VerificationPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Update the target session's lastUsed time...");
        try {
            String query =
                    "UPDATE sessions SET lastUsed = ? WHERE sessionID = ?;";
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setTimestamp(1,currentTime);
            ps.setString(2,requestModel.getSessionID());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded.");
            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to update the target session's lastUsed time.");
            e.printStackTrace();
            return false;
        }
    }

    public static String createNewLoginSessionToDb(LoginPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Generate a new session and insert it to database...");
        if (!revokeExistingSession(requestModel)) {
            return null;
        }
        try {
            String query =
                    "INSERT INTO sessions(sessionID, email, status, timeCreated, lastUsed, exprTime) VALUES(?,?,1,?,?,?);";

            Session new_session = Session.createSession(requestModel.getEmail());

            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,new_session.getSessionID().toString());
            ps.setString(2,requestModel.getEmail());
            ps.setTimestamp(3,new_session.getTimeCreated());
            ps.setTimestamp(4,new_session.getLastUsed());
            ps.setTimestamp(5,new_session.getExprTime());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded.");
            return new_session.getSessionID().toString();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to put new session into database.");
            e.printStackTrace();
            return null;
        }
    }

    public static String createNewLoginSessionToDb(VerificationPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Generate a new session and insert it to database...");
        if (!revokeExistingSession(requestModel, 4)){
            return null;
        }
        try {
            String query =
                    "INSERT INTO sessions(sessionID, email, status, timeCreated, lastUsed, exprTime) VALUES(?,?,1,?,?,?);";
            Session new_session = Session.createSession(requestModel.getEmail());

            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,new_session.getSessionID().toString());
            ps.setString(2,requestModel.getEmail());
            ps.setTimestamp(3,new_session.getTimeCreated());
            ps.setTimestamp(4,new_session.getLastUsed());
            ps.setTimestamp(5,new_session.getExprTime());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded.");
            return new_session.getSessionID().toString();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to put new session into database.");
            e.printStackTrace();
            return null;
        }
    }

    public static String verifySessionFromDb(VerificationPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Verify the given session's status...");
        try {
            String query =
                    "SELECT status, timeCreated, lastUsed, exprTime FROM sessions WHERE email = ? AND sessionID = ?";

            PreparedStatement ps1 = IDMService.getCon().prepareStatement(query);
            ps1.setString(1, requestModel.getEmail());
            ps1.setString(2, requestModel.getSessionID());

            ServiceLogger.LOGGER.info("Trying query: " + ps1.toString());
            ResultSet rs1 = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query Successful.");
            Session retrieve = null;
            int stat = 0;

            if(rs1.next()) {
                stat = rs1.getInt("status");
                retrieve = Session.rebuildSession(
                        requestModel.getEmail(),
                        Token.rebuildToken(requestModel.getSessionID()),
                        rs1.getTimestamp("timeCreated"),
                        rs1.getTimestamp("lastUsed"),
                        rs1.getTimestamp("exprTime")
                        );
            }

            if(retrieve == null) {
                ServiceLogger.LOGGER.info("Session not found.");
                return "134";
            }
            else if (stat == 3) {
                ServiceLogger.LOGGER.info("Session is expired.");
                return "131";
            }
            else if (stat == 2) {
                ServiceLogger.LOGGER.info("Session is closed.");
                return "132";
            }
            else if (stat == 4) {
                ServiceLogger.LOGGER.info("Session is revoked.");
                return "133";
            }
            else if (stat == 1) {
                ServiceLogger.LOGGER.info("Session is currently active.");
                if (!retrieve.isExpired()) {
                    ServiceLogger.LOGGER.info("Session expired, turning status to 3.");
                    if (!revokeExistingSession(requestModel, 3)) {
                        return null;
                    }
                    return "131";
                }
                else if (!retrieve.isTimeout()) {
                    ServiceLogger.LOGGER.info("Session timeout, triggers revoke, turning status to 4.");
                    if(!revokeExistingSession(requestModel,4)) {
                        return null;
                    }
                    return "133";
                }
                else{
                    Timestamp exprTimeout = new Timestamp(retrieve.getExprTime().getTime()-IDMService.getConfigs().getTimeout());
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    if (currentTime.after(exprTimeout)) {
                        ServiceLogger.LOGGER.info("Revoke the old session and create a new session.");
                        String newID = createNewLoginSessionToDb(requestModel);
                        if (newID == null) {
                            return null;
                        }
                        else {
                            return newID;
                        }
                    }
                    else {
                        if (!lastUsedUpdate(requestModel)) {
                            return null;
                        }
                        return requestModel.getSessionID();
                    }
                }
            }
            else {
                ServiceLogger.LOGGER.info("Unknown session status code.");
                return null;
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform verification.");
            e.printStackTrace();
            return null;
        }
    }

    public static int retrievePrivilegeLevelFromDb(PrivilegePageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Start retrieving the privilege level of the target user.");
        int userValidation = isUserInDb(requestModel);
        if (userValidation == 0) {
            return 0; // User not found
        }
        else if (userValidation == -1) {
            return -1; // Internal server error
        }
        try {
            String query =
                    "SELECT plevel FROM users WHERE email = ?;";

            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            int privilegeLevel = -1;

            if (rs.next()) {
                privilegeLevel = rs.getInt("plevel");
                ServiceLogger.LOGGER.info("User privilege level retrieved.");
                return privilegeLevel;
            }
            else {
                ServiceLogger.LOGGER.warning("Unexpected error when doing database query.");
                return -1;
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve privilege level of the target user.");
            e.printStackTrace();
            return -1;
        }
    }
}

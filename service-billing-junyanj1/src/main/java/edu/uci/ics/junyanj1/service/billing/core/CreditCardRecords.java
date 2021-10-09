package edu.uci.ics.junyanj1.service.billing.core;

import edu.uci.ics.junyanj1.service.billing.BillingService;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.CreditCardInfoRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CreditCardManipulationRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CreditCardModel;

import java.sql.*;

public class CreditCardRecords {
    public static int InsertCCRToDb(CreditCardInfoRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Attempting to insert credit card record to the database...");

        try {
            String query =
                    "INSERT INTO creditcards(id,firstName,lastName,expiration)\n"+
                            "VALUES(?,?,?,?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getId());
            ps.setString(2,requestModel.getFirstName());
            ps.setString(3,requestModel.getLastName());
            ps.setDate(4,requestModel.getExpiration());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            return 3200;
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.warning("Violating the unique pair insertion constraint.");
                return 325;
            } else {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Unable to perform insertion.");
                return -1;
            }
        }
    }

    public static int UpdateCCRInDb(CreditCardInfoRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Attempting to update credit card record to the database...");

        try {
            String query =
                    "UPDATE creditcards\n"+
                            "SET firstName = ?, lastName = ?, expiration = ?\n"+
                            "WHERE id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getFirstName());
            ps.setString(2,requestModel.getLastName());
            ps.setDate(3, requestModel.getExpiration());
            ps.setString(4,requestModel.getId());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rowsUpdated = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rowsUpdated == 0) {
                return 324;
            }

            return 3210;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform update.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int DeleteCCRFromDb(CreditCardManipulationRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Attempting to delete credit card record in the database...");

        try {
            String query =
                    "DELETE FROM creditcards\n" +
                            "WHERE id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getId());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rowsDeleted = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rowsDeleted == 0) {
                return 324;
            }

            return 3220;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform update.");
            e.printStackTrace();
            return -1;
        }
    }

    public static CreditCardModel RetrieveCCRFromDb(CreditCardManipulationRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Attempting to retrieve credit card record from the database...");

        try {
            String query =
                    "SELECT id, firstName, lastName, expiration\n" +
                            "FROM creditcards\n" +
                            "WHERE id = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getId());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            CreditCardModel target = new CreditCardModel("nonExist");
            if (rs.next()) {
                target.setId(rs.getString("id"));
                target.setFirstName(rs.getString("firstName"));
                target.setLastName(rs.getString("lastName"));
                target.setExpiration(rs.getDate("expiration"));
            }

            return target;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform update.");
            e.printStackTrace();
            return null;
        }
    }
}

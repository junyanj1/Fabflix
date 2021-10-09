package edu.uci.ics.junyanj1.service.billing.core;

import edu.uci.ics.junyanj1.service.billing.BillingService;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

public class ShoppingCartRecords {
    public static int InsertSCRToDb(ShoppingCartInsertRequestModel scirm) {
        ServiceLogger.LOGGER.info("Attempting to insert shopping cart record to the database...");

        try {
            String query =
                    "INSERT INTO carts(email, movieId,quantity)\n"+
                            "VALUES(?,?,?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,scirm.getEmail());
            ps.setString(2,scirm.getMovieId());
            ps.setInt(3,scirm.getQuantity());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            return 3100;
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.warning("Violating the unique pair insertion constraint.");
                return 311;
            } else {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Unable to perform insertion.");
                return -1;
            }
        }
    }

    public static int UpdateSCRInDb(ShoppingCartUpdateRequestModel scurm) {
        ServiceLogger.LOGGER.info("Attempting to insert shopping cart record to the database...");

        try {
            String query =
                    "UPDATE carts\n"+
                            "SET quantity = ?\n"+
                            "WHERE email = ? AND movieId = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setInt(1,scurm.getQuantity());
            ps.setString(2,scurm.getEmail());
            ps.setString(3,scurm.getMovieId());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rowsUpdated = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rowsUpdated == 0) {
                return 312;
            }

            return 3110;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform update.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int DeleteSCRFromDb(ShoppingCartDeleteRequestModel scdrm) {
        ServiceLogger.LOGGER.info("Attempting to delete shopping cart record from the database...");

        try {
            String query =
                    "DELETE FROM carts\n"+
                            "WHERE email = ? AND movieId = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,scdrm.getEmail());
            ps.setString(2,scdrm.getMovieId());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int rowsUpdated = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            if(rowsUpdated == 0) {
                return 312;
            }

            return 3120;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete.");
            e.printStackTrace();
            return -1;
        }
    }

    public static ArrayList<ShoppingCartModel> RetrieveSCRFromDb(ShoppingCartRetrieveAndClearRequestModel scracrm) {
        ServiceLogger.LOGGER.info("Attempting to retrieve shopping cart record from the database...");

        try {
            String query =
                    "SELECT email, movieId, quantity\n" +
                            "FROM carts\n" +
                            "WHERE email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,scracrm.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<ShoppingCartModel> scr = new ArrayList<ShoppingCartModel>();
            while (rs.next()) {
                scr.add(new ShoppingCartModel(rs.getString("email"),
                        rs.getString("movieId"),
                        rs.getInt("quantity"))
                );
            }

            return scr;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean ClearAllSCRFromDb(String email) {
        ServiceLogger.LOGGER.info("Attempting to clear specified user's shopping cart record from the database...");

        try {
            String query =
                    "DELETE FROM carts\n"+
                            "WHERE email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform clear.");
            e.printStackTrace();
            return false;
        }
    }
}

package edu.uci.ics.junyanj1.service.billing.core;

import com.paypal.api.payments.Payment;
import edu.uci.ics.junyanj1.service.billing.BillingService;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.*;

import java.sql.*;
import java.util.ArrayList;

public class OrderRecords {
//    public static ArrayList<OrderModel> CheckOut(OrderRequestModel requestModel) {
//        ServiceLogger.LOGGER.info("Checking if the user has items in shopping cart...");
//
//        try {
//            String query =
//                    "SELECT email, movieId, quantity\n" +
//                            "FROM carts\n" +
//                            "WHERE email = ?;";
//            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
//            ps.setString(1, requestModel.getEmail());
//
//            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
//            ResultSet rs = ps.executeQuery();
//            ServiceLogger.LOGGER.info("Query successful.");
//
//            ArrayList<OrderModel> preOrder = new ArrayList<OrderModel>();
//            while (rs.next()) {
//                preOrder.add(new OrderModel(rs.getString("email"),
//                        rs.getString("movieId"),
//                        rs.getInt("quantity"),
//                        new Date(new java.util.Date().getTime()))
//                );
//            }
//
//            return preOrder;
//        } catch (SQLException e) {
//            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public static boolean insertOrderToDb(String email, ArrayList<OrderModel> orders) {
//        ServiceLogger.LOGGER.info("Inserting orders to sales table in database...");
//
//        try {
//            String query =
//                    "INSERT INTO sales(email,movieId,quantity,saleDate)\n" +
//                            "VALUES(?,?,?,?)\n";
//            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
//            for (OrderModel o : orders) {
//                ps.setString(1,o.getEmail());
//                ps.setString(2,o.getMovieId());
//                ps.setInt(3,o.getQuantity());
//                ps.setDate(4,o.getSaleDate());
//                ps.addBatch();
//            }
//
//            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
//            int[] numUpdates = ps.executeBatch();
//            ServiceLogger.LOGGER.info("Query successful.");
//
//            if (!ShoppingCartRecords.ClearAllSCRFromDb(email))
//                return false;
//
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
//            return false;
//        }
//    }

    public static ArrayList<CheckOutModel> CheckOut(OrderRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Checking if the user has items in shopping cart...");

        try {
            String query =
                    "SELECT email, carts.movieId, quantity, unit_price, discount\n" +
                            "FROM carts, movie_prices\n" +
                            "WHERE email = ? AND carts.movieId = movie_prices.movieId;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

//            float sum = 0;
            ArrayList<CheckOutModel> checkouts = new ArrayList<CheckOutModel>();
            while (rs.next()) {
//                sum += rs.getFloat("unit_price") * rs.getInt("quantity") * rs.getFloat("discount");
                checkouts.add(new CheckOutModel(rs.getString("email"),
                        rs.getString("movieId"),
                        rs.getInt("quantity"),
                        rs.getFloat("unit_price"),
                        rs.getFloat("discount")));
            }

            return checkouts;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean insertOrderToDb(String token, ArrayList<CheckOutModel> checkouts) {
        ServiceLogger.LOGGER.info("Inserting orders to sales table in database...");

        try {
            String query =
                    "{call InsertSalesTransactions(?,?,?,?)}\n";
            CallableStatement ps = BillingService.getCon().prepareCall(query);
            for (CheckOutModel c : checkouts) {
                ps.setString(1,c.getEmail());
                ps.setString(2,c.getMovieId());
                ps.setInt(3,c.getQuantity());
                ps.setString(4,token);
                ps.addBatch();
            }

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int[] numUpdates = ps.executeBatch();
            ServiceLogger.LOGGER.info("Query successful.");

            String email = FromTokenToEmail(token);
            if (email != null) {
                if (!ShoppingCartRecords.ClearAllSCRFromDb(email))
                    return false;
            } else
                return false;

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
            return false;
        }
    }

//    public static ArrayList<OrderModel> RetrieveOrdersFromDb(OrderRequestModel requestModel) {
//        ServiceLogger.LOGGER.info("Retrieving orders from database...");
//
//        try {
//            String query =
//                    "SELECT movieId, quantity, saleDate\n" +
//                            "FROM sales\n" +
//                            "WHERE email = ?;";
//
//            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
//            ps.setString(1,requestModel.getEmail());
//
//            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
//            ResultSet rs = ps.executeQuery();
//            ServiceLogger.LOGGER.info("Query successful.");
//
//            ArrayList<OrderModel> orders = new ArrayList<OrderModel>();
//            while (rs.next()) {
//                orders.add(new OrderModel(requestModel.getEmail(),
//                        rs.getString("movieId"),
//                        rs.getInt("quantity"),
//                        rs.getDate("saleDate")));
//            }
//
//            return orders;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            ServiceLogger.LOGGER.warning("Unable to perform retrieval.");
//            return null;
//        }
//    }

    public static Boolean PutTransactionIdInDb(Payment createdPayment, String token) {
        ServiceLogger.LOGGER.info("Payment completed, inserting transactionId in database...");

        try {
            String query =
                    "UPDATE transactions SET transactionId = ? WHERE token = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
            ps.setString(2,token);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int updateRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

//            String email = FromTokenToEmail(token);
//            if (email != null) {
//                if (!ShoppingCartRecords.ClearAllSCRFromDb(email))
//                    return null;
//            } else
//                return null;

            if (updateRows == 0)
                return false;

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
            return null;
        }
    }

    public static String FromTokenToEmail(String token) {
        ServiceLogger.LOGGER.info("Trying to get the email of the user in this transaction...");

        try {
            String query =
                    "SELECT email FROM sales, transactions WHERE transactions.token = ? AND transactions.sId = sales.id;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,token);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next()) {
                return rs.getString("email");
            }

            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to perform search.");
            return null;
        }
    }

    public static ArrayList<TransactionModel> FromEmailToTransactions(String email) {
        ServiceLogger.LOGGER.info("Trying to get the email of the user in this transaction...");

        try {
            String query =
                    "SELECT DISTINCT transactionId FROM sales, transactions WHERE sales.email = ? AND transactions.sId = sales.id;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<TransactionModel> transactions = new ArrayList<TransactionModel>();
            while (rs.next()) {
                if (rs.getString("transactionId")!=null)
                    transactions.add(new TransactionModel(rs.getString("transactionId")));
            }

            return transactions;
        }catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to perform search.");
            return null;
        }
    }

    public static ArrayList<ItemModel> RetrieveItemsFromDb(String transactionId) {
        ServiceLogger.LOGGER.info("Trying to get items by transactionId...");

        try {
            String query =
                    "SELECT email,sales.movieId AS movieId, quantity, unit_price, discount, saleDate\n" +
                            "FROM sales, transactions, movie_prices\n" +
                            "WHERE transactions.transactionId = ?\n" +
                            "AND sales.movieId = movie_prices.movieId\n" +
                            "AND transactions.sId = sales.id;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,transactionId);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<ItemModel> items = new ArrayList<ItemModel>();
            while (rs.next()) {
                items.add(new ItemModel(rs.getString("email"),
                        rs.getString("movieId"),
                        rs.getInt("quantity"),
                        rs.getFloat("unit_price"),
                        rs.getFloat("discount"),
                        rs.getDate("saleDate")));
            }

            return items;
        }catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to perform search.");
            return null;
        }
    }
}

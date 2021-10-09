package edu.uci.ics.junyanj1.service.billing.core;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.junyanj1.service.billing.BillingService;
import edu.uci.ics.junyanj1.service.billing.configs.Configs;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import org.glassfish.grizzly.servlet.HttpServletRequestImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayPalClient {
    public static String clientId = "ARxyt5jAAoagTwKSwGUiD-9TKf7Zcqz9TITZ4KS6yxUvUL_wx8vqXRo0w8YpdtOQDIX9zXtzeNAwwlc2";
    public static String clientSecret = "EGmxDzeMVE7pjBYHe1MAh8Nzlq-7uAcBJaFRlPIFPxXb_pb7j0zYa2I8p-imHhAvf-fYmBSvtfUDPh3H";

    public Map<String, String> createPayment(String sum) {
        Map<String,String> response = new HashMap<String, String>();
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        Configs conf = BillingService.getConfigs();
        String host = conf.getHostName();
        if (host.equals("0.0.0.0"))
            host = "localhost";
//        redirectUrls.setReturnUrl(conf.getScheme()+host+":"+conf.getPort()+conf.getPath()+"/order/complete");
        redirectUrls.setReturnUrl("http://128.195.6.93:3154/html/checkoutcomplete.html");
        redirectUrls.setCancelUrl(conf.getScheme()+host+":"+conf.getPort()+conf.getPath()+"/order/cancel");
        payment.setRedirectUrls(redirectUrls);
        Payment createdPayment;

        try {
            String redirectUrl = "";
            APIContext context = new APIContext(clientId,clientSecret,"sandbox");
            createdPayment = payment.create(context);
            if(createdPayment!=null) {
                List<Links> links = createdPayment.getLinks();
                for (Links link : links) {
                    if(link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status","success");
                response.put("redirect_url",redirectUrl);
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Error happened during payment creation!");
            response.put("status","failure");
        }
        return response;
    }
}

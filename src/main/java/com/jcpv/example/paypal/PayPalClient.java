package com.jcpv.example.paypal;

import com.jcpv.example.util.ConstantsPaypal;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by jancarlo on 8/03/18.
 */
@Component
public class PayPalClient {

    private static Logger log = LoggerFactory.getLogger(PayPalClient.class);


    @Value("${paypal.url.cancel:http://localhost:4200/cancel}")
    private String urlCancel;

    @Value("${paypal.url.cancel:http://localhost:4200/process}")
    private String urlProcess;

    public Map<String, Object> createPayment(String sum){

        Map<String, Object> response = new HashMap<String, Object>();
        //set Payer details
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        //Set redirect Urls
        RedirectUrls redirectUrls = new RedirectUrls();
        String guid = UUID.randomUUID().toString().replaceAll("-", "");
        log.info(""+urlCancel+"/"+ guid);
        redirectUrls.setCancelUrl(urlCancel);
        redirectUrls.setReturnUrl(urlProcess);

        //Set payment details optional
        Details details = new Details();
        details.setShipping("1");
        details.setTax("1");
        details.setSubtotal("5");

        //Set payment Amount
        Amount amount = new Amount();
        amount.setCurrency("MXN");
        //amount.setDetails(details);
        amount.setTotal("7");

        //Set transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);

        //Set Item optional
        Item item = new Item();
        item.setName("Coffe ").setQuantity("1").setCurrency("MXN").setPrice("5");
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        itemList.setItems(items);

        //transaction.setItemList(itemList);

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        //Set Payment

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        try {
            Payment createdPayment;
            String redirectUrl = "";
            APIContext context = new APIContext(ConstantsPaypal.clientID, ConstantsPaypal.clientSecret,ConstantsPaypal.mode);
            createdPayment = payment.create(context);

            log.info("Created payment with id = "
                    + createdPayment.getId() + " and status = "
                    + createdPayment.getState());
            // ###Payment Approval Url
            Iterator<Links> links = createdPayment.getLinks().iterator();
            while (links.hasNext()) {
                Links link = links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    redirectUrl= link.getHref();
                }

                response.put("status","success");
                response.put("redirecurl",redirectUrl);
            }

        } catch (PayPalRESTException e) {
            log.error(e.toString());

        }
    return response;

    }

    public Map<String,Object> completePayment(HttpServletRequest request){
        Map<String, Object> response= new HashMap<String,Object>();
        Payment payment = new Payment();
        payment.setId(request.getParameter("paymentId"));
        PaymentExecution paymentExecution= new PaymentExecution();
        paymentExecution.setPayerId(request.getParameter("PayerID"));

        try {
            APIContext context = new APIContext(ConstantsPaypal.clientID, ConstantsPaypal.clientSecret,ConstantsPaypal.mode);
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment!=null){
                response.put("status", "success");
                response.put("payment", createdPayment);
            }


        }catch (PayPalRESTException expay){

        }
        return response;
    }

}

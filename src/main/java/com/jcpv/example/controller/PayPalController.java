package com.jcpv.example.controller;

import com.jcpv.example.paypal.PayPalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by jancarlo on 9/03/18.
 */
@RestController
public class PayPalController {

    @Autowired
    private PayPalClient payPalClient;

    @PostMapping(value = "/make/payment")
    public Map<String, Object> createPayment(@RequestParam("sum") String sum){
        return payPalClient.createPayment(sum);
    }

    @PostMapping(value = "/complete/payment")
    public Map<String, Object> completePayment(HttpServletRequest request, @RequestParam("paymentId") String paymentId, @RequestParam("payerId") String payerId){
        return payPalClient.completePayment(request);
    }
}

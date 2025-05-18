package com.husc.lms.controller;

import com.husc.lms.service.PaypalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paypal")
public class PaypalController {

    @Autowired
    private PaypalService paypalService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestParam("sum") double sum) {
        try {
            String approvalLink = paypalService.createPayment(
                sum,
                "USD",
                "paypal",
                "sale",
                "Thanh toán đơn hàng",
                "http://localhost:8080/lms/paypal/cancel",
                "http://localhost:8080/lms/paypal/success"
            );

            if (approvalLink != null) {
                return ResponseEntity.ok(approvalLink);
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return ResponseEntity.internalServerError().body("Không thể tạo thanh toán.");
    }

    @GetMapping("/success")
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if ("approved".equalsIgnoreCase(payment.getState())) {
            	paypalService.successPayment(payment);
                return "Thanh toán thành công!";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "Thanh toán thất bại!";
    }

    @GetMapping("/cancel")
    public String cancelPay() {
        return "Đã huỷ thanh toán!";
    }
}

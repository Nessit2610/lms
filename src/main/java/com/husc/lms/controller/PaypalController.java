package com.husc.lms.controller;

import com.husc.lms.dto.request.PaymentRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CourseViewResponse;

import com.husc.lms.service.PaypalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paypal")
public class PaypalController {

    @Autowired
    private PaypalService paypalService;
    

    @PostMapping("/pay")
    public APIResponse<Boolean> pay(@Valid @RequestBody PaymentRequest request) {
        try {
            String approvalLink = paypalService.createPayment(request);

            if (approvalLink != null) {
                return APIResponse.<Boolean>builder()
                		.result(true)
                		.message(approvalLink)
                		.build();
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return APIResponse.<Boolean>builder()
        		.result(false)
        		.message("Không thể tạo thanh toán !")
        		.build();
    }
    
    @GetMapping("/success")
    public APIResponse<Boolean> successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if ("approved".equalsIgnoreCase(payment.getState())) {
            	paypalService.successPayment(payment);
                return APIResponse.<Boolean>builder()
                		.result(true)
                		.message("Thanh toán thành công !")
                		.build();
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return APIResponse.<Boolean>builder()
        		.result(false)
        		.message("Thanh toán thất bại !")
        		.build();
    }

    @GetMapping("/cancel")
    public APIResponse<Boolean> cancelPay() {
    	return APIResponse.<Boolean>builder()
        		.result(false)
        		.message("Thanh toán thất bại !")
        		.build();
    }
}

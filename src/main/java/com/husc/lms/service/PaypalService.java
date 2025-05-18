package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Course;
import com.husc.lms.entity.OrderEntity;
import com.husc.lms.entity.PaymentEntity;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.PaymentEntityRepository;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PaypalService {
	@Autowired
	private APIContext apiContext;
	
	@Autowired
	private PaymentEntityRepository paymentEntityRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	public String createPayment(double total, String currency, String method,
            String intent, String description,
            String cancelUrl, String successUrl) throws PayPalRESTException {
		Amount amount = new Amount();
		amount.setCurrency(currency);
		amount.setTotal(String.format("%.2f", total));
		
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);
		
		List<Transaction> transactions = Collections.singletonList(transaction);
		
		Payer payer = new Payer();
		payer.setPaymentMethod(method);
		
		Payment payment = new Payment();
		payment.setIntent(intent);
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);
		
		Payment createdPayment = payment.create(apiContext);
		
		for (Links link : createdPayment.getLinks()) {
				if (link.getRel().equalsIgnoreCase("approval_url")) {
					return link.getHref();
			}
		}
		
		return null;
}
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
	}

//	public Boolean processPayment(OrderEntity order) {
//        try {
//            Payment payment = createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
//                    order.getIntent(), order.getDescription(), "http://localhost:8080/lms/paypal/pay/cancel",
//                    "http://localhost:8080/lms/paypal/pay/success");
//
//            for (Links link : payment.getLinks()) {
//                if (link.getRel().equals("approval_url")) {
//                    return true;
//                }
//            }
//        } catch (PayPalRESTException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public boolean successPayment(Payment payment) {
        System.out.println(payment.toJSON());
		//Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		if ("approved".equals(payment.getState())) {
		    PaymentEntity pay = new PaymentEntity();
		    pay.setPaymentId(payment.getId());
		    //pay.setCourse(course);
		    pay.setDescription(payment.getTransactions().get(0).getDescription());
		    pay.setCreateTime(payment.getCreateTime());
		    pay.setCountryCode(payment.getTransactions().get(0).getItemList().getShippingAddress().getCountryCode());
		    pay.setEmail(payment.getPayer().getPayerInfo().getEmail());
		    pay.setPostalCode(payment.getTransactions().get(0).getItemList().getShippingAddress().getPostalCode());
		    pay.setTotalPrice(Float.parseFloat(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getAmount().getTotal()));
		    pay.setStatus(payment.getPayer().getStatus());
		    pay.setTransactionFee(Float.parseFloat(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getTransactionFee().getValue()));

		    paymentEntityRepository.save(pay);

		    return true;
		}
        return false;
    }
}


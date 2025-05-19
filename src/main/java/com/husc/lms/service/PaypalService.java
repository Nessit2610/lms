package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.PaymentRequest;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.OrderEntity;
import com.husc.lms.entity.PaymentEntity;
import com.husc.lms.entity.PendingPayment;
import com.husc.lms.entity.Student;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.PaymentEntityRepository;
import com.husc.lms.repository.PendingPaymentRepository;
import com.husc.lms.repository.StudentRepository;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.servlet.http.HttpSession;

@Service
public class PaypalService {
	@Autowired
	private APIContext apiContext;
	
	@Autowired
	private PaymentEntityRepository paymentEntityRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private PendingPaymentRepository pendingPaymentRepository;
	
	@Autowired
	private StudentCourseService studentCourseService;
	
	public String createPayment(PaymentRequest request) throws PayPalRESTException {
		Amount amount = new Amount();
		amount.setCurrency(request.getCurrency());
		amount.setTotal(String.format("%.2f", request.getPrice()));
		
		Transaction transaction = new Transaction();
		transaction.setDescription(request.getDescription());
		transaction.setAmount(amount);
		
		List<Transaction> transactions = Collections.singletonList(transaction);
		
		Payer payer = new Payer();
		payer.setPaymentMethod(request.getMethod());
		
		Payment payment = new Payment();
		payment.setIntent(request.getIntent());
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(request.getCancelUrl());
		redirectUrls.setReturnUrl(request.getSuccessUrl());
		payment.setRedirectUrls(redirectUrls);
		
		Payment createdPayment = payment.create(apiContext);
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(()-> new AppException(ErrorCode.COURSE_NOT_FOUND));
		
        PendingPayment pending = new PendingPayment();
        pending.setPaymentId(createdPayment.getId());
        pending.setStudent(student); // lấy từ context hoặc param
        pending.setCourse(course);   // lấy từ param hoặc trước đó
        pending.setCompleted(false);

        pendingPaymentRepository.save(pending);
        
		for (Links link : createdPayment.getLinks()) {
				if (link.getRel().equalsIgnoreCase("approval_url")) {
					return link.getHref();
			}
		}
		
		return null;
	}
	
	public CourseViewResponse preparePayment(String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return courseMapper.toCourseViewResponse(course);
    }
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
	}

    public boolean successPayment(Payment payment) {
    	PendingPayment pending = pendingPaymentRepository.findById(payment.getId())
    	        .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

	    Student student = pending.getStudent();
	    Course course = pending.getCourse();
		if ("approved".equals(payment.getState())) {
		    PaymentEntity pay = new PaymentEntity();
		    pay.setPaymentId(payment.getId());
		    pay.setCourse(course);
		    pay.setStudent(student);
		    pay.setDescription(payment.getTransactions().get(0).getDescription());
		    pay.setCreateTime(payment.getCreateTime());
		    pay.setCountryCode(payment.getTransactions().get(0).getItemList().getShippingAddress().getCountryCode());
		    pay.setEmail(payment.getPayer().getPayerInfo().getEmail());
		    pay.setPostalCode(payment.getTransactions().get(0).getItemList().getShippingAddress().getPostalCode());
		    pay.setTotalPrice(Float.parseFloat(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getAmount().getTotal()));
		    pay.setStatus(payment.getPayer().getStatus());
		    pay.setTransactionFee(Float.parseFloat(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getTransactionFee().getValue()));
		    pending.setCompleted(true);
		    pendingPaymentRepository.save(pending);
		    paymentEntityRepository.save(pay);
		    studentCourseService.addStudentBuyCourse(student, course);
		    
		    return true;
		}
        return false;
    }
}


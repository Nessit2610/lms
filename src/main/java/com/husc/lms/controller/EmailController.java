package com.husc.lms.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@PostMapping("/send")
	public APIResponse<Void> sendConfirmationEmail(@RequestParam("email") String email) throws UnsupportedEncodingException{
		emailService.sendConfirmationEmail(email);
		return APIResponse.<Void>builder()
				.code(200)
				.message("Email sending")
				.build();
	}
	
	@PostMapping("/forgotpassword")
	public APIResponse<Void> sendPasswordEmail(@RequestParam("email") String email) throws UnsupportedEncodingException{
		emailService.sendPasswordEmail(email);
		return APIResponse.<Void>builder()
				.code(200)
				.message("Email sending")
				.build();
	}
	
	@PostMapping("/verifycode")
	public APIResponse<Boolean> verifyCode(@RequestParam("email") String email,
											@RequestParam("code") String code){
		return APIResponse.<Boolean>builder()
				.result(emailService.verifyCode(email, code))
				.build();
	}
	
}

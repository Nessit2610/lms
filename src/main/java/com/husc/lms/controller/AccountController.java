package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.husc.lms.dto.request.PasswordRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@PutMapping("/changePassword")
	public APIResponse<Void> changePassword(@RequestBody @Valid PasswordRequest request) {
		 accountService.changePassword(request) ;
		 return APIResponse.<Void>builder()
				 .code(200)
				 .message("change password success")
				 .build();
	}
	
	
	
	//ADMIN API 
	
	
	@GetMapping
	public APIResponse<List<AccountResponse>> getAllAccount(){
		return APIResponse.<List<AccountResponse>>builder()
				.result(accountService.getAllAccount())
				.build();
	}
	
	@GetMapping("/{accountId}")
	public APIResponse<AccountResponse> getAccount(@PathVariable("accountId") String id){
		return APIResponse.<AccountResponse>builder()
				.result(accountService.getAccountById(id))
				.build();
	}
	
	@GetMapping("/details/{accountId}")
	public APIResponse<Object> getAccountDetails(@PathVariable("accountId") String id){
		return APIResponse.<Object>builder()
				.result(accountService.getAccountDetails(id))
				.build();
	}
}

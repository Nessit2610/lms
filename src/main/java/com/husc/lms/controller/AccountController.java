package com.husc.lms.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import com.husc.lms.dto.request.PasswordRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public APIResponse<Page<AccountResponse>> getAllAccount(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<AccountResponse>>builder()
				.result(accountService.getAllAccount(pageNumber, pageSize))
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
	
	@PutMapping("/changeactive")
	public APIResponse<Boolean> changeActive(@RequestParam("accountId") String accountId, @RequestParam("active") boolean active){
		return APIResponse.<Boolean>builder()
				.result(accountService.setActiveAccount(accountId, active))
				.build();
	}
	@PutMapping("/{id}/roles")
    public APIResponse<Account> updateRoles(@PathVariable String id,@RequestBody Set<String> roleIds) {
		return APIResponse.<Account>builder()
				.result(accountService.updateAccountRoles(id, roleIds))
				.build();
       
    }
	
}

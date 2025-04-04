package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.AccountRequest;
import com.husc.lms.dto.request.PasswordRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.service.AccountService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private AccountService userService;
	
	@PutMapping("/changePassword")
	public AccountResponse UpdateUser(@RequestBody PasswordRequest request) {
		return userService.changePassword(request) ;
	}
	
	@DeleteMapping("/{userId}")
	public String DeleteUser(@PathVariable("userId") String Id) {
		userService.DeleteUser(Id);
		return "User has been deleted";
	}
}

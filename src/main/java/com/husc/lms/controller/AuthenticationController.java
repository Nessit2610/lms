package com.husc.lms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.AuthenticationRequest;
import com.husc.lms.dto.request.IntrospectRequest;
import com.husc.lms.dto.request.LogoutRequest;
import com.husc.lms.dto.request.RefreshRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.AuthenticationResponse;
import com.husc.lms.dto.response.IntrospectResponse;
import com.husc.lms.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/token")
	public APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
		var result = authenticationService.Authenticate(request);
		return APIResponse.<AuthenticationResponse>builder()
				.result(result)
				.build();
	}
	
	@PostMapping("/introspect")
	public APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws JOSEException, ParseException{
		var result = authenticationService.introspect(request);
		return APIResponse.<IntrospectResponse>builder()
				.result(result)
				.build();
	}	
	
	@PostMapping("/logout")
	public APIResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
		authenticationService.logout(request);
		return APIResponse.<Void>builder().build();
	}
	
	@PostMapping("/refresh")
	public APIResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request) throws JOSEException, ParseException{
		var result = authenticationService.refreshToken(request);
		return APIResponse.<AuthenticationResponse>builder()
				.result(result)
				.build();
	}
	 
}

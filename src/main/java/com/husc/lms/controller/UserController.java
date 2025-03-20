package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.request.UserUpdateRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.UserMongo;
import com.husc.lms.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/users")
	public APIResponse<UserResponse> CreateUser(@RequestBody @Valid UserCreationRequest request) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(userService.createUser(request));
		return apiResponse;
	}
	
	@GetMapping("/users")
	public List<UserResponse> GetAllUser() {
		var authenticate = SecurityContextHolder.getContext().getAuthentication();
		return userService.GetAllUser();
	}
	
	@GetMapping("/users/{userId}")
	public UserResponse GetUser(@PathVariable("userId") String id) {
		return userService.GetUserById(id);
	}
	
	@GetMapping("/myinfo")
	public APIResponse<UserResponse> GetMyInfo() {
		return APIResponse.<UserResponse>builder()
				.result(userService.getMyInfo())
				.build(); 
	}
	
	@PutMapping("/users/{userId}")
	public UserResponse UpdateUser(@PathVariable("userId") String id ,@RequestBody UserUpdateRequest updateRequest) {
		return userService.UpdateUser(id, updateRequest) ;
	}
	
	@DeleteMapping("/users/{userId}")
	public String DeleteUser(@PathVariable("userId") String Id) {
		userService.DeleteUser(Id);
		return "User has been deleted";
	}
	 @GetMapping("/users/mongo")
    public List<UserMongo> getUsersFromMongo() {
        return userService.getAllUsersMongo();
    }
	 @PostMapping("/users/mongo")
    public UserMongo createUserInMongo(@RequestBody UserMongo user) {
        return userService.saveUserToMongo(user);
    }
}

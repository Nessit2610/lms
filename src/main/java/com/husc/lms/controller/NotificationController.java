package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.NotificationResponse;
import com.husc.lms.service.NotificationService;

@RestController
@RequestMapping("/noti")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	
	@PostMapping("/create")
	public APIResponse<NotificationResponse> createNotification(@RequestBody NotificationRequest request){
		APIResponse<NotificationResponse> apiResponse = new APIResponse<NotificationResponse>();
		apiResponse.setResult(notificationService.createNotification(request));
		return apiResponse;
		
	}
	
	@GetMapping("/{notiId}")
	public APIResponse<NotificationResponse> getNoti(@PathVariable("notiId") String id){
		return APIResponse.<NotificationResponse>builder()
				.result(notificationService.getNoti(id))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<NotificationResponse>> getAllNoti(){
		return APIResponse.<List<NotificationResponse>>builder()
				.result(notificationService.getAllNoti())
				.build();
	}
	
}

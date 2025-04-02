package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.NotificationResponse;
import com.husc.lms.entity.Notification;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.NotificationMapper;
import com.husc.lms.repository.NotificationRepository;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;
	
	
	@Autowired
	private NotificationMapper notificationMapper;
	
	
	public NotificationResponse createNotification(NotificationRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Notification notification = Notification.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.detail(request.getDetail())
				.type(request.getType())
				.createdBy(name)
				.createdDate(new Date())
				.lastModifiedBy(name)
				.lastModifiedDate(new Date())
				.build();
		notification = notificationRepository.save(notification);
		
		NotificationResponse notificationResponse = NotificationResponse.builder()
				.title(notification.getTitle())
				.description(notification.getDescription())
				.detail(notification.getDetail())
				.type(notification.getType())
				.build();
		return notificationResponse;
	}
	
	
	public List<NotificationResponse> getAllNoti(String type) {
		String publicTybe = "1";
		var Notis = notificationRepository.findByType(publicTybe);
		
		if(!(type == null || type.isEmpty())) {
			Notis = notificationRepository.findByType(type);
		}
	
		return Notis.stream().map(notificationMapper::toNotificationResponse).toList();
	}
	
	public NotificationResponse getNoti(String id) {
		Notification noti = notificationRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.CODE_ERROR));
		return notificationMapper.toNotificationResponse(noti);
	}
}

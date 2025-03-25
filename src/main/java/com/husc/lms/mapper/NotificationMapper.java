package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.NotificationResponse;
import com.husc.lms.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

	public NotificationResponse toNotificationResponse(Notification noti);
}

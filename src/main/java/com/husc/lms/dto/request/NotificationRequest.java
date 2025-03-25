package com.husc.lms.dto.request;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
	
	private String title;
	
	private String description;
	
	private String detail;
	
	private String type;
}

package com.husc.lms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterResponse {
	
	private String id;
	
	private String name;
	
	private String path;
 
    private String type;
    
    private Integer order;
}

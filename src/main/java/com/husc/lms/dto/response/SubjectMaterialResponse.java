package com.husc.lms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectMaterialResponse {

	private String name;

    private String fileName;

    private String contentType;
    
    private String filePath;
}

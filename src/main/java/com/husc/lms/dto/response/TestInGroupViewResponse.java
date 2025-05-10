package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestInGroupViewResponse {
	
	private String id;

    private String title;

    private String description;
    
    private OffsetDateTime startedAt;
    
    private OffsetDateTime createdAt;

    private OffsetDateTime expiredAt;
}

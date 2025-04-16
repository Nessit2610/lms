package com.husc.lms.dto.response;

import java.time.OffsetDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentChapterResponse {
    private String username;
    private String detail;
    private OffsetDateTime createdDate;
}

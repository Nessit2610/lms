package com.husc.lms.dto.response;

import java.util.Date;

import com.husc.lms.mongoEntity.ChatBoxMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBoxMemberResponse {
	private String id;

    private String chatBoxId;

    private String accountUsername;
    
    private String accountFullname;
    
    private String avatar;

    private Date joinedAt;
}

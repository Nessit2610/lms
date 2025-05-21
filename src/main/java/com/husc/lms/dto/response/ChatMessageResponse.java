package com.husc.lms.dto.response;

import java.util.Date;
import java.util.List;

import com.husc.lms.dto.response.ChatBoxResponse.MemberAccountInChatBox;
import com.husc.lms.mongoEntity.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
	
	private String id;

    private String chatBoxId;

    private String content;

    private Date createdAt;

    private String path;

    private String type;

    private String filename;
        
    private List<SenderAccount> senderAccount;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SenderAccount {
    	private String accountId;
    	private String accountUsername;
    	private String accountFullname;
    	private String avatar;
    }
}

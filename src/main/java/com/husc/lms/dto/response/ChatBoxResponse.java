package com.husc.lms.dto.response;

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
public class ChatBoxResponse {
	private String id;

    private boolean isGroup;

    private Date createdAt;

    private String createdBy;

    private String name;
    
    private String avatar;
    
    private Date updatedAt;

    private String lastMessage;

    private Date lastMessageAt;

    private String lastMessageBy;
    
    private List<MemberAccountInChatBox> memberAccountUsernames;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberAccountInChatBox {
    	private String accountId;
    	private String accountUsername;
    	private String accountFullname;
    	private String avatar;
    }

}

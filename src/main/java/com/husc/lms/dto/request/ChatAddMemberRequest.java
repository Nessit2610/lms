package com.husc.lms.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAddMemberRequest {
    private String chatBoxId;
    private String usernameOfMemberToAdd;
    private String usernameOfRequestor; // Sẽ được set ở backend từ principal hoặc token nếu cần bảo mật hơn
}
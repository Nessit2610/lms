package com.husc.lms.mongoService;

import com.husc.lms.dto.response.ChatBoxMemberResponse;
import com.husc.lms.dto.response.ChatMemberSearchResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ChatBoxMemberService {
    ChatBox addMemberToChatBox(String chatBoxId, String usernameOfMemberToAdd, String usernameOfRequestor);

//    List<ChatBoxMember> getChatBoxMembersByChatBoxId(String chatBoxId);
    
    List<ChatBoxMemberResponse> getChatBoxMembersByChatBoxId(String chatBoxId);


    void removeMemberFromChatBox(String chatBoxId, String usernameOfMemberToRemove, String usernameOfRequestor);

    Page<ChatMemberSearchResponse> findAccountsNotInChatBox(String chatBoxId, String searchString, int pageNumber,
            int pageSize);
    
}

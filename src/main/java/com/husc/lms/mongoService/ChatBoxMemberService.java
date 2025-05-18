//package com.husc.lms.mongoService;
//
//import com.husc.lms.dto.response.ChatMemberSearchResponse;
//import com.husc.lms.entity.Account;
//import com.husc.lms.mongoEntity.ChatBox;
//import com.husc.lms.mongoEntity.ChatBoxMember;
//import java.util.List;
//
//public interface ChatBoxMemberService {
//    ChatBox addMemberToChatBox(String chatBoxId, String usernameOfMemberToAdd, String usernameOfRequestor);
//
//    List<ChatBoxMember> getChatBoxMembersByChatBoxId(String chatBoxId);
//
//    void removeMemberFromChatBox(String chatBoxId, String usernameOfMemberToRemove, String usernameOfRequestor);
//
//    List<ChatMemberSearchResponse> findAccountsNotInChatBox(String chatBoxId, String searchString);
//}

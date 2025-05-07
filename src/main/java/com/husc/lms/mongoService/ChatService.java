//package com.husc.lms.mongoService;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import com.husc.lms.mongoEntity.ChatBox;
//import com.husc.lms.mongoEntity.ChatMessage;
//
//public interface ChatService {
//	public ChatBox getOrCreatePrivateChatBox(String senderId, String receiverId);
//
//	public ChatMessage saveMessage(String chatBoxId, String senderId, String content);
//
//	public Page<ChatBox> getOneToOneChatBoxesForAccount(Pageable pageable);
//}

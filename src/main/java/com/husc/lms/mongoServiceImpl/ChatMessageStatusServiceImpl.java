package com.husc.lms.mongoServiceImpl;

import org.springframework.stereotype.Service;

import com.husc.lms.mongoService.ChatMessageStatusService;
import com.husc.lms.mongoRepository.ChatMessageStatusRepository;
import com.husc.lms.mongoEntity.ChatMessageStatus;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatMessageStatusServiceImpl implements ChatMessageStatusService {

    private final ChatMessageStatusRepository chatMessageStatusRepository;

    @Override
    public void markAllUnreadMessagesAsRead(String chatBoxId) {
        List<ChatMessageStatus> unreadMessages = chatMessageStatusRepository.findByChatBoxIdAndIsReadFalse(chatBoxId);
        Date now = new Date();
        for (ChatMessageStatus message : unreadMessages) {
            message.setRead(true);
            message.setReadAt(now);
        }
        chatMessageStatusRepository.saveAll(unreadMessages);
    }
}

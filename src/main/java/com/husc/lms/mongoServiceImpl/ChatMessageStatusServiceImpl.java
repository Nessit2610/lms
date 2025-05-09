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
    public void markMessagesAsRead(String chatBoxId, String username) {
        // Find all unread statuses for the given user in the given chatbox
        // This requires a custom method in ChatMessageStatusRepository
        List<ChatMessageStatus> unreadStatuses = chatMessageStatusRepository
                .findByChatBoxIdAndAccountUsernameAndIsReadFalse(chatBoxId, username);

        if (unreadStatuses != null && !unreadStatuses.isEmpty()) {
            unreadStatuses.forEach(status -> {
                status.setRead(true);
                status.setReadAt(new Date());
            });
            chatMessageStatusRepository.saveAll(unreadStatuses);
            System.out.println("[DEBUG] ChatMessageStatusService: Marked " + unreadStatuses.size() +
                    " messages as read for user '" + username + "' in chatBoxId '" + chatBoxId + "'.");
        } else {
            System.out.println("[DEBUG] ChatMessageStatusService: No unread messages found for user '" + username +
                    "' in chatBoxId '" + chatBoxId + "' to mark as read.");
        }
    }
}

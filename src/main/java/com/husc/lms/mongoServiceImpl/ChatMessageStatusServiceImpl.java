package com.husc.lms.mongoServiceImpl;

import org.springframework.stereotype.Service;

import com.husc.lms.mongoService.ChatMessageStatusService;
import com.husc.lms.mongoRepository.ChatMessageStatusRepository;
import com.husc.lms.mongoEntity.ChatMessageStatus;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.time.OffsetDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatMessageStatusServiceImpl implements ChatMessageStatusService {

    private final ChatMessageStatusRepository chatMessageStatusRepository;

    @Override
    public void markMessagesAsRead(String chatBoxId, String username) {
        List<ChatMessageStatus> unreadStatuses = chatMessageStatusRepository
                .findByChatBoxIdAndAccountUsernameAndIsReadFalse(chatBoxId, username);

        if (unreadStatuses != null && !unreadStatuses.isEmpty()) {
            unreadStatuses.forEach(status -> {
                status.setRead(true);
                status.setReadAt(OffsetDateTime.now());
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

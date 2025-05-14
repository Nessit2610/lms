package com.husc.lms.mongoServiceImpl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.husc.lms.mongoEntity.ChatMessageStatus;
import com.husc.lms.mongoRepository.ChatMessageStatusRepository;
import com.husc.lms.mongoService.ChatMessageStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageStatusServiceImpl implements ChatMessageStatusService {

    private final ChatMessageStatusRepository statusRepo;

    private OffsetDateTime convertToOffsetDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
    }

    @Override
    public List<ChatMessageStatus> getMessageStatuses(String messageId) {
        return statusRepo.findByMessageId(messageId);
    }

    @Override
    public void markMessagesAsRead(String chatBoxId, String accountUsername) {
        List<ChatMessageStatus> unreadStatuses = statusRepo.findByChatBoxIdAndAccountUsernameAndIsReadFalse(
                chatBoxId, accountUsername);
        Date now = new Date();
        for (ChatMessageStatus status : unreadStatuses) {
            status.setRead(true);
            status.setReadAt(now);
        }
        statusRepo.saveAll(unreadStatuses);
    }
}
